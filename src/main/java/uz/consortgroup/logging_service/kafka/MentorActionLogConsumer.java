package uz.consortgroup.logging_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.event.mentor.MentorResourceActionEvent;
import uz.consortgroup.logging_service.service.processor.MentorActionProcessor;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class MentorActionLogConsumer extends AbstractKafkaConsumer<MentorResourceActionEvent> {
    private final MentorActionProcessor mentorActionProcessor;

    @KafkaListener(
            topics = "${kafka.mentor-action}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void onMessage(List<MentorResourceActionEvent> events, Acknowledgment ack) {
        log.info("Received {} user-created events", events.size());
        processBatch(events, ack);
    }

    @Override
    protected void handleMessage(MentorResourceActionEvent event) {
        mentorActionProcessor.process(List.of(event));
    }

    @Override
    protected UUID getMessageId(MentorResourceActionEvent event) {
        return event.getMessageId();
    }
}
