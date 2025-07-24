package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.entity.enumeration.MentorActionType;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.service.MentorActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MentorActionProcessor implements ActionProcessor<MentorActionEvent, MentorActionType> {

    private final MentorActionService mentorActionService;

    @Override
    public boolean canHandle(MentorActionType actionType) {
        log.info("Checking handler support for mentor action type: {}", actionType);
        return false;
    }

    @Override
    public void process(List<MentorActionEvent> events) {
        if (events == null || events.isEmpty()) {
            log.info("No mentor action events received for processing.");
            return;
        }

        log.info("Processing {} mentor action event(s).", events.size());
        mentorActionService.saveMentorActions(events);
        log.info("Finished processing mentor action events.");
    }
}
