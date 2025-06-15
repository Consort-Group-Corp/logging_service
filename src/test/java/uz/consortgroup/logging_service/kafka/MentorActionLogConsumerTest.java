package uz.consortgroup.logging_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.logging_service.event.mentor.MentorResourceActionEvent;
import uz.consortgroup.logging_service.service.processor.MentorActionProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class MentorActionLogConsumerTest {

    @Mock
    private MentorActionProcessor mentorActionProcessor;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private MentorActionLogConsumer consumer;

    @Test
    void shouldProcessMessagesSuccessfully() {
        MentorResourceActionEvent event = new MentorResourceActionEvent();
        consumer.onMessage(List.of(event), acknowledgment);
        verify(mentorActionProcessor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleEmptyMessageList() {
        consumer.onMessage(List.of(), acknowledgment);
        verify(acknowledgment).acknowledge();
        verifyNoInteractions(mentorActionProcessor);
    }

    @Test
    void shouldSkipNullMessages() {
        MentorResourceActionEvent event = new MentorResourceActionEvent();
        List<MentorResourceActionEvent> messages = new ArrayList<>();
        messages.add(event);
        messages.add(null);
        consumer.onMessage(messages, acknowledgment);
        verify(mentorActionProcessor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleProcessorFailure() {
        MentorResourceActionEvent event = new MentorResourceActionEvent();
        doThrow(new RuntimeException()).when(mentorActionProcessor).process(any());
        assertDoesNotThrow(() -> consumer.onMessage(List.of(event), acknowledgment));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleAckFailure() {
        MentorResourceActionEvent event = new MentorResourceActionEvent();
        doThrow(new RuntimeException()).when(acknowledgment).acknowledge();
        assertThrows(RuntimeException.class, () -> consumer.onMessage(List.of(event), acknowledgment));
        verify(mentorActionProcessor).process(List.of(event));
    }
}