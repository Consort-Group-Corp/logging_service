package uz.consortgroup.logging_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.logging_service.entity.ActionType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    protected void processBatch(List<T> messages, Acknowledgment ack) {
        List<CompletableFuture<Void>> futures = messages.stream()
                .filter(Objects::nonNull)
                .map(message -> CompletableFuture.runAsync(() -> {
                    try {
                        handleMessage(message);
                    } catch (Exception e) {
                        log.error("Error processing message {}: ", getMessageId(message), e);
                    }
                }))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error waiting for batch to complete", e);
        } finally {
            ack.acknowledge();
        }
    }

    protected abstract void handleMessage(T message);
    protected abstract ActionType actionType();
    protected abstract UUID getMessageId(T message);
}
