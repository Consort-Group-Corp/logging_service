package uz.consortgroup.logging_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.logging_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.logging_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.logging_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.logging_service.entity.HrAction;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.repository.HrActionRepository;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrActionService {
    private final HrActionRepository hrActionRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void saveHrActions(List<HrActionEvent> events) {
        List<HrAction> actions = events.stream()
                .filter(event -> markIfNotProcessed(event.getMessageId()))
                .map(event -> HrAction.builder()
                        .hrId(event.getHrId())
                        .userId(event.getUserId())
                        .hrActionType(event.getHrActionType())
                        .createdAt(event.getCreatedAt())
                        .build())
                .toList();
        try {
            hrActionRepository.saveAll(actions);
        } catch (Exception e) {
            throw new RuntimeException("Database save failed", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "hr_action_event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        return Boolean.TRUE.equals(wasSet);
    }
}
