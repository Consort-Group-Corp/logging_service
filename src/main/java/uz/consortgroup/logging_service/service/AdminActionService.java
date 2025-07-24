package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.entity.SuperAdminAction;
import uz.consortgroup.logging_service.event.admin.SuperAdminActionEvent;
import uz.consortgroup.logging_service.repository.AdminActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminActionService {
    private final AdminActionRepository adminActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void saveAdminActions(List<SuperAdminActionEvent> events) {
        if (events.isEmpty()) {
            log.warn("No SuperAdminActionEvents received for saving.");
            return;
        }

        log.info("Processing {} SuperAdminActionEvent(s).", events.size());

        List<SuperAdminAction> actions = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> {
                    boolean notProcessed = markIfNotProcessed(event.getMessageId());
                    if (!notProcessed) {
                        log.debug("Event with messageId={} has already been processed. Skipping.", event.getMessageId());
                    }
                    return notProcessed;
                })
                .map(event -> {
                    log.debug("Mapping event to entity: {}", event);
                    return SuperAdminAction.builder()
                            .adminId(event.getAdminId())
                            .userId(event.getUserId())
                            .userEmail(event.getEmail())
                            .userRole(event.getRole())
                            .superAdminActionType(event.getSuperAdminActionType())
                            .createdAt(event.getCreatedAt())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        try {
            adminActionRepository.saveAll(actions);
            log.info("Successfully saved {} SuperAdminAction(s) to the database.", actions.size());
        } catch (Exception e) {
            log.error("Failed to save SuperAdminActions to the database.", e);
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "super_admin_event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, "true", Duration.ofHours(1));
        log.debug("Redis key set for messageId={}: {}", messageId, wasSet);
        return Boolean.TRUE.equals(wasSet);
    }
}
