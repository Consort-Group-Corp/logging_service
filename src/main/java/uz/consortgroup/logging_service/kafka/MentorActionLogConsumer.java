package uz.consortgroup.logging_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.service.processor.MentorActionProcessor;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class MentorActionLogConsumer extends AbstractKafkaConsumer<MentorActionEvent> {
    private final MentorActionProcessor mentorActionProcessor;

    @KafkaListener(
            topics = "${topics.mentor-action}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void onMessage(List<MentorActionEvent> events, Acknowledgment ack) {
        log.info("Received {} user-created events", events.size());
        processBatch(events, ack);
    }

    @Override
    protected void handleMessage(MentorActionEvent event) {
        mentorActionProcessor.process(List.of(event));
    }

    @Override
    protected UUID getMessageId(MentorActionEvent event) {
        return event.getMessageId();
    }
}
