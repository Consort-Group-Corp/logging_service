package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.entity.MentorAction;
import uz.consortgroup.logging_service.event.mentor.MentorResourceActionEvent;
import uz.consortgroup.logging_service.repository.MentorActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorActionService {
    private final MentorActionRepository mentorActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void saveMentorActions(List<MentorResourceActionEvent> events) {
        if (events.isEmpty()) return;

        List<MentorAction> actions = events.stream()
                .filter(event -> markIfNotProcessed(event.getMessageId()))
                .map(event -> MentorAction.builder()
                        .mentorId(event.getMentorId())
                        .resourceId(event.getResourceId())
                        .mentorActionType(event.getMentorActionType())
                        .createdAt(event.getCreatedAt())
                        .build())
                .toList();

        log.info("Saving {} mentor actions", actions.size());

        try {
            mentorActionRepository.saveAll(actions);
            log.info("Saved mentor actions successfully");
        } catch (Exception e) {
            log.error("Failed to save mentor actions", e);
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        return Boolean.TRUE.equals(wasSet);
    }
}
