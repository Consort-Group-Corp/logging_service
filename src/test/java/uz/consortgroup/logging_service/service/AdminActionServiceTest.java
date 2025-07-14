package uz.consortgroup.logging_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.entity.enumeration.UserRole;
import uz.consortgroup.logging_service.event.admin.SuperAdminActionEvent;
import uz.consortgroup.logging_service.repository.AdminActionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminActionServiceTest {

    @Mock
    private AdminActionRepository adminActionRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private AdminActionService adminActionService;

    @Test
    void shouldSaveAdminActionsSuccessfully() {
        SuperAdminActionEvent event = createTestEvent();
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        adminActionService.saveAdminActions(List.of(event));

        verify(adminActionRepository).saveAll(anyList());
    }

    @Test
    void shouldSkipAlreadyProcessedEvents() {
        SuperAdminActionEvent event = createTestEvent();
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(false);

        adminActionService.saveAdminActions(List.of(event));

        verify(adminActionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldHandleEmptyEventList() {
        adminActionService.saveAdminActions(List.of());
        verifyNoInteractions(adminActionRepository);
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void shouldSkipNullEvents() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        List<SuperAdminActionEvent> events = new ArrayList<>();
        events.add(createTestEvent());
        events.add(null);

        adminActionService.saveAdminActions(events);

        verify(adminActionRepository).saveAll(anyList());
    }

    @Test
    void shouldHandleDatabaseError() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        when(adminActionRepository.saveAll(anyList())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                adminActionService.saveAdminActions(List.of(createTestEvent())));
    }

    private SuperAdminActionEvent createTestEvent() {
        return SuperAdminActionEvent.builder()
            .messageId(UUID.randomUUID())
            .adminId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .email("test@example.com")
            .role(UserRole.SUPER_ADMIN)
            .superAdminActionType(SuperAdminActionType.USER_CREATED)
            .createdAt(LocalDateTime.now())
            .build();
    }
}