package uz.consortgroup.logging_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.service.processor.HrActionProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class HrActionLogConsumerTest {

    @Mock
    private HrActionProcessor hrActionProcessor;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private HrActionLogConsumer consumer;

    @Test
    void shouldProcessMessagesSuccessfully() {
        HrActionEvent event = new HrActionEvent();
        consumer.onMessage(List.of(event), acknowledgment);
        verify(hrActionProcessor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleEmptyMessageList() {
        consumer.onMessage(List.of(), acknowledgment);
        verify(acknowledgment).acknowledge();
        verifyNoInteractions(hrActionProcessor);
    }

    @Test
    void shouldSkipNullMessages() {
        HrActionEvent event = new HrActionEvent();
        List<HrActionEvent> messages = new ArrayList<>();
        messages.add(event);
        messages.add(null);
        consumer.onMessage(messages, acknowledgment);
        verify(hrActionProcessor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleProcessorFailure() {
        HrActionEvent event = new HrActionEvent();
        doThrow(new RuntimeException()).when(hrActionProcessor).process(any());
        assertDoesNotThrow(() -> consumer.onMessage(List.of(event), acknowledgment));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleAckFailure() {
        HrActionEvent event = new HrActionEvent();
        doThrow(new RuntimeException()).when(acknowledgment).acknowledge();
        assertThrows(RuntimeException.class, () -> consumer.onMessage(List.of(event), acknowledgment));
        verify(hrActionProcessor).process(List.of(event));
    }
}