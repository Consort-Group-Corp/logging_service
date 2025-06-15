package uz.consortgroup.logging_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.logging_service.event.admin.SuperAdminUserActionEvent;
import uz.consortgroup.logging_service.service.processor.SuperAdminActionProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AdminActionLogConsumerTest {

    @Mock
    private SuperAdminActionProcessor processor;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private AdminActionLogConsumer consumer;

    @Test
    void shouldProcessMessagesSuccessfully() {
        SuperAdminUserActionEvent event = new SuperAdminUserActionEvent();
        consumer.onMessage(List.of(event), acknowledgment);
        verify(processor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleEmptyMessageList() {
        consumer.onMessage(List.of(), acknowledgment);
        verify(acknowledgment).acknowledge();
        verifyNoInteractions(processor);
    }

    @Test
    void shouldSkipNullMessages() {
        SuperAdminUserActionEvent event = new SuperAdminUserActionEvent();
        List<SuperAdminUserActionEvent> messages = new ArrayList<>();
        messages.add(event);
        messages.add(null);

        consumer.onMessage(messages, acknowledgment);

        verify(processor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleProcessorFailure() {
        SuperAdminUserActionEvent event = new SuperAdminUserActionEvent();
        doThrow(new RuntimeException()).when(processor).process(any());
        consumer.onMessage(List.of(event), acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleAckFailure() {
        SuperAdminUserActionEvent event = new SuperAdminUserActionEvent();

        doThrow(new RuntimeException("Ack failed")).when(acknowledgment).acknowledge();

        assertThrows(RuntimeException.class, () ->
                consumer.onMessage(List.of(event), acknowledgment));

        verify(processor).process(List.of(event));
    }
}