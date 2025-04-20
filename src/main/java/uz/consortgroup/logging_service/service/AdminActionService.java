package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.entity.AdminAction;
import uz.consortgroup.logging_service.event.admin.UserCreatedEvent;
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
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void saveAdminActions(List<UserCreatedEvent> events) {

        if (events.isEmpty()) {
            return;
        }

        List<AdminAction> actions = events.stream()
                .filter(event -> markIfNotProcessed(event.getMessageId()))
                .map(event -> AdminAction.builder()
                        .adminId(event.getAdminId())
                        .userId(event.getUserId())
                        .userEmail(event.getEmail())
                        .userRole(event.getRole())
                        .actionType(event.getActionType())
                        .createdAt(event.getCreatedAt())
                        .build())
                .filter(Objects::nonNull)
                .toList();

        log.info("Saving {} admin actions", actions.size());

        try {
            adminActionRepository.saveAll(actions);
            log.info("Successfully saved {} actions", actions.size());
        } catch (Exception e) {
            log.error("Failed to save admin actions", e);
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "event_processed:" + messageId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue()
                        .setIfAbsent(key, "true", Duration.ofMinutes(1))
        );
    }
}
