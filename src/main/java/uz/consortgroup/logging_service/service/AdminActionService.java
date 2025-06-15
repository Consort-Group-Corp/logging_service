package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.asspect.annotation.AllAspect;
import uz.consortgroup.logging_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.logging_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.logging_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.logging_service.entity.SuperAdminAction;
import uz.consortgroup.logging_service.event.admin.SuperAdminUserActionEvent;
import uz.consortgroup.logging_service.repository.AdminActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminActionService {
    private final AdminActionRepository adminActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void saveAdminActions(List<SuperAdminUserActionEvent> events) {

        if (events.isEmpty()) {
            return;
        }

        List<SuperAdminAction> actions = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> markIfNotProcessed(event.getMessageId()))
                .map(event -> SuperAdminAction.builder()
                        .adminId(event.getAdminId())
                        .userId(event.getUserId())
                        .userEmail(event.getEmail())
                        .userRole(event.getRole())
                        .superAdminActionType(event.getSuperAdminActionType())
                        .createdAt(event.getCreatedAt())
                        .build())
                .filter(Objects::nonNull)
                .toList();

        try {
            adminActionRepository.saveAll(actions);
        } catch (Exception e) {
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "super_admin_event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        return Boolean.TRUE.equals(wasSet);
    }
}
