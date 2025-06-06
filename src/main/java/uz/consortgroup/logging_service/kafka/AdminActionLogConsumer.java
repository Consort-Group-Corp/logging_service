package uz.consortgroup.logging_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.event.admin.SuperAdminUserActionEvent;
import uz.consortgroup.logging_service.service.processor.SuperAdminActionProcessor;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class AdminActionLogConsumer extends AbstractKafkaConsumer<SuperAdminUserActionEvent> {
    private final SuperAdminActionProcessor processor;

    public AdminActionLogConsumer(SuperAdminActionProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(
            topics = "${kafka.super-admin-action}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void onMessage(List<SuperAdminUserActionEvent> events, Acknowledgment ack) {
        log.info("Received {} user-created events", events.size());
        processBatch(events, ack);
    }

    @Override
    protected void handleMessage(SuperAdminUserActionEvent event) {
        processor.process(List.of(event));
    }

    @Override
    protected SuperAdminActionType actionType() {
        return SuperAdminActionType.USER_CREATED;
    }

    @Override
    protected UUID getMessageId(SuperAdminUserActionEvent event) {
        return event.getMessageId();
    }
}
