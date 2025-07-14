package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.logging_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.logging_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.logging_service.entity.MentorAction;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.repository.MentorActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorActionService {
    private final MentorActionRepository mentorActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void saveMentorActions(List<MentorActionEvent> events) {
        if (events.isEmpty()) return;

        List<MentorAction> actions = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> markIfNotProcessed(event.getMessageId()))
                .map(event -> MentorAction.builder()
                        .mentorId(event.getMentorId())
                        .resourceId(event.getResourceId())
                        .mentorActionType(event.getMentorActionType())
                        .createdAt(event.getCreatedAt())
                        .build())
                .toList();

        try {
            mentorActionRepository.saveAll(actions);
        } catch (Exception e) {
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "mentor_action_event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        return Boolean.TRUE.equals(wasSet);
    }
}
