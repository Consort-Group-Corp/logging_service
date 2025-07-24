package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.entity.HrAction;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.repository.HrActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrActionService {
    private final HrActionRepository hrActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void saveHrActions(List<HrActionEvent> events) {
        if (events.isEmpty()) {
            log.warn("No HrActionEvents received for saving.");
            return;
        }

        log.info("Processing {} HrActionEvent(s).", events.size());

        List<HrAction> actions = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> {
                    boolean notProcessed = markIfNotProcessed(event.getMessageId());
                    if (!notProcessed) {
                        log.debug("Event with messageId={} was already processed. Skipping.", event.getMessageId());
                    }
                    return notProcessed;
                })
                .map(event -> {
                    log.debug("Mapping event to entity: {}", event);
                    return HrAction.builder()
                            .hrId(event.getHrId())
                            .userId(event.getUserId())
                            .hrActionType(event.getHrActionType())
                            .createdAt(event.getCreatedAt())
                            .build();
                })
                .toList();

        try {
            hrActionRepository.saveAll(actions);
            log.info("Successfully saved {} HrAction(s) to the database.", actions.size());
        } catch (Exception e) {
            log.error("Failed to save HrActions to the database.", e);
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "hr_action_event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        log.debug("Redis key set for messageId={}: {}", messageId, wasSet);
        return Boolean.TRUE.equals(wasSet);
    }
}
