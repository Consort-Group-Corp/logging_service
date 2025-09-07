package uz.consortgroup.logging_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.logging_service.event.admin.SuperAdminActionEvent;
import uz.consortgroup.logging_service.service.processor.SuperAdminActionProcessor;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminActionLogConsumer extends AbstractKafkaConsumer<SuperAdminActionEvent> {
    private final SuperAdminActionProcessor processor;

    @KafkaListener(
            topics = "${topics.super-admin-action}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void onMessage(List<SuperAdminActionEvent> events, Acknowledgment ack) {
        log.info("Received {} user-created events", events.size());
        processBatch(events, ack);
    }

    @Override
    protected void handleMessage(SuperAdminActionEvent event) {
        processor.process(List.of(event));
    }


    @Override
    protected UUID getMessageId(SuperAdminActionEvent event) {
        return event.getMessageId();
    }
}
