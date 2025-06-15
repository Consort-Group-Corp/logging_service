package uz.consortgroup.logging_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import uz.consortgroup.logging_service.entity.enumeration.HrActionType;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.repository.HrActionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrActionServiceTest {

    @Mock
    private HrActionRepository hrActionRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private HrActionService hrActionService;

    @Test
    void shouldSaveHrActionsSuccessfully() {
        HrActionEvent event = createTestEvent();
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        hrActionService.saveHrActions(List.of(event));

        verify(hrActionRepository).saveAll(anyList());
    }

    @Test
    void shouldSkipAlreadyProcessedEvents() {
        HrActionEvent event = createTestEvent();
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(false);

        hrActionService.saveHrActions(List.of(event));

        verify(hrActionRepository).saveAll(List.of());
    }

    @Test
    void shouldHandleEmptyEventList() {
        hrActionService.saveHrActions(List.of());
        verify(hrActionRepository).saveAll(List.of());
    }

    @Test
    void shouldSkipNullEvents() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        List<HrActionEvent> events = new ArrayList<>();
        events.add(createTestEvent());
        events.add(null);

        hrActionService.saveHrActions(events);

        verify(hrActionRepository).saveAll(anyList());
    }

    @Test
    void shouldHandleDatabaseError() {
        HrActionEvent event = createTestEvent();
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        when(hrActionRepository.saveAll(anyList())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                hrActionService.saveHrActions(List.of(event)));
    }

    private HrActionEvent createTestEvent() {
        return HrActionEvent.builder()
                .messageId(UUID.randomUUID())
                .hrId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .hrActionType(HrActionType.FORUM_GROUP_CREATED)
                .createdAt(LocalDateTime.now())
                .build();
    }
}