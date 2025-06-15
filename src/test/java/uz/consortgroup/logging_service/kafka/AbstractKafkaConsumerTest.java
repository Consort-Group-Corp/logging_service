package uz.consortgroup.logging_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractKafkaConsumerTest {

    @Mock
    private Acknowledgment acknowledgment;

    @Spy
    private TestKafkaConsumer consumer;

    private static class TestKafkaConsumer extends AbstractKafkaConsumer<TestMessage> {
        @Override
        protected void handleMessage(TestMessage message) {
        }

        @Override
        protected UUID getMessageId(TestMessage message) {
            return message.id();
        }
    }

    private record TestMessage(UUID id) {}

    @Test
    void shouldProcessAllValidMessages() {
        TestMessage msg1 = new TestMessage(UUID.randomUUID());
        TestMessage msg2 = new TestMessage(UUID.randomUUID());
        consumer.processBatch(List.of(msg1, msg2), acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldSkipNullMessages() {
        TestMessage msg = new TestMessage(UUID.randomUUID());
        List<TestMessage> messages = new ArrayList<>();
        messages.add(msg);
        messages.add(null);

        consumer.processBatch(messages, acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleMessageProcessingFailure() {
        TestKafkaConsumer spyConsumer = spy(new TestKafkaConsumer());
        TestMessage msg = new TestMessage(UUID.randomUUID());

        doThrow(new RuntimeException()).when(spyConsumer).handleMessage(any());

        spyConsumer.processBatch(List.of(msg), acknowledgment);

        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldAcknowledgeOnExecutionException() {
        TestMessage msg = new TestMessage(UUID.randomUUID());
        consumer.processBatch(List.of(msg), acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldAcknowledgeOnInterruptedException() {
        TestMessage msg = new TestMessage(UUID.randomUUID());
        consumer.processBatch(List.of(msg), acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleEmptyMessageList() {
        consumer.processBatch(List.of(), acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleAllNullMessages() {
        List<TestMessage> messages = new ArrayList<>();
        messages.add(null);
        messages.add(null);

        consumer.processBatch(messages, acknowledgment);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleAcknowledgmentFailure() {
        TestMessage msg = new TestMessage(UUID.randomUUID());

        doThrow(new RuntimeException("Ack failed")).when(acknowledgment).acknowledge();

        assertThrows(RuntimeException.class, () ->
                consumer.processBatch(List.of(msg), acknowledgment));

        verify(consumer).handleMessage(msg);
    }
}