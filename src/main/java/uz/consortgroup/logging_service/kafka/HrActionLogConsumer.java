package uz.consortgroup.logging_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.service.processor.HrActionProcessor;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class HrActionLogConsumer extends AbstractKafkaConsumer<HrActionEvent>  {
    private final HrActionProcessor hrActionProcessor;

    @Override
    protected void handleMessage(HrActionEvent event) {
         hrActionProcessor.process(List.of(event));
    }

    @KafkaListener(
            topics = "${kafka.hr-action}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void onMessage(List<HrActionEvent> events, Acknowledgment ack) {
        log.info("Received {} user-created events", events.size());
        processBatch(events, ack);
    }

    @Override
    protected UUID getMessageId(HrActionEvent event) {
        return event.getMessageId();
    }
}
