package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.entity.MentorAction;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.repository.MentorActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorActionService {
    private final MentorActionRepository mentorActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void saveMentorActions(List<MentorActionEvent> events) {
        if (events.isEmpty()) {
            log.warn("No MentorActionEvents to process.");
            return;
        }

        log.info("Processing {} MentorActionEvent(s).", events.size());

        List<MentorAction> actions = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> {
                    boolean notProcessed = markIfNotProcessed(event.getMessageId());
                    if (!notProcessed) {
                        log.debug("Event with messageId={} was already processed. Skipping.", event.getMessageId());
                    }
                    return notProcessed;
                })
                .map(event -> {
                    log.debug("Mapping event to MentorAction: {}", event);
                    return MentorAction.builder()
                            .mentorId(event.getMentorId())
                            .resourceId(event.getResourceId())
                            .mentorActionType(event.getMentorActionType())
                            .createdAt(event.getCreatedAt())
                            .build();
                })
                .toList();

        try {
            mentorActionRepository.saveAll(actions);
            log.info("Successfully saved {} MentorAction(s) to the database.", actions.size());
        } catch (Exception e) {
            log.error("Failed to save MentorActions to the database.", e);
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "mentor_action_event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        log.debug("Redis key set for messageId={}: {}", messageId, wasSet);
        return Boolean.TRUE.equals(wasSet);
    }
}
