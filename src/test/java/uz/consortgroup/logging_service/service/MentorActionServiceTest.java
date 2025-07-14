package uz.consortgroup.logging_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import uz.consortgroup.logging_service.entity.enumeration.MentorActionType;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.repository.MentorActionRepository;

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
class MentorActionServiceTest {

    @Mock
    private MentorActionRepository mentorActionRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private MentorActionService mentorActionService;

    @Test
    void shouldSaveMentorActionsSuccessfully() {
        MentorActionEvent event = createTestEvent();
        mockRedisSuccess();

        mentorActionService.saveMentorActions(List.of(event));

        verify(mentorActionRepository).saveAll(anyList());
    }

    @Test
    void shouldSkipAlreadyProcessedEvents() {
        MentorActionEvent event = createTestEvent();
        mockRedisAlreadyProcessed();

        mentorActionService.saveMentorActions(List.of(event));

        verify(mentorActionRepository).saveAll(List.of());
    }


    @Test
    void shouldSkipNullEvents() {
        mockRedisSuccess();
        List<MentorActionEvent> events = new ArrayList<>();
        events.add(createTestEvent());
        events.add(null);

        mentorActionService.saveMentorActions(events);

        verify(mentorActionRepository).saveAll(anyList());
    }

    @Test
    void shouldHandleDatabaseError() {
        MentorActionEvent event = createTestEvent();
        mockRedisSuccess();
        when(mentorActionRepository.saveAll(anyList())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> 
            mentorActionService.saveMentorActions(List.of(event)));
    }

    private void mockRedisSuccess() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
            .thenReturn(true);
    }

    private void mockRedisAlreadyProcessed() {
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
            .thenReturn(false);
    }

    private MentorActionEvent createTestEvent() {
        return MentorActionEvent.builder()
            .messageId(UUID.randomUUID())
            .mentorId(UUID.randomUUID())
            .resourceId(UUID.randomUUID())
            .mentorActionType(MentorActionType.COURSE_CREATED)
            .createdAt(LocalDateTime.now())
            .build();
    }
}