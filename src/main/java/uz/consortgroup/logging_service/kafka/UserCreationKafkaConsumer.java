package uz.consortgroup.logging_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.logging_service.entity.ActionType;
import uz.consortgroup.logging_service.event.admin.UserCreatedEvent;
import uz.consortgroup.logging_service.service.processor.UserCreationProcessor;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class UserCreationKafkaConsumer extends AbstractKafkaConsumer<UserCreatedEvent> {
    private final UserCreationProcessor processor;

    public UserCreationKafkaConsumer(UserCreationProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(
            topics = "${kafka.user-created}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void onMessage(List<UserCreatedEvent> events, Acknowledgment ack) {
        log.info("Received {} user-created events", events.size());
        processBatch(events, ack);
    }

    @Override
    protected void handleMessage(UserCreatedEvent event) {
        processor.process(List.of(event));
    }

    @Override
    protected ActionType actionType() {
        return ActionType.USER_CREATED;
    }

    @Override
    protected UUID getMessageId(UserCreatedEvent event) {
        return event.getMessageId();
    }
}
