package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.entity.enumeration.HrActionType;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.service.HrActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class HrActionProcessor implements ActionProcessor<HrActionEvent, HrActionType> {

    private final HrActionService hrActionService;

    @Override
    public boolean canHandle(HrActionType actionType) {
        log.info("Checking handler support for action type: {}", actionType);
        return true;
    }

    @Override
    public void process(List<HrActionEvent> events) {
        if (events == null || events.isEmpty()) {
            log.info("No HR action events received for processing.");
            return;
        }

        log.info("Processing {} HR action event(s).", events.size());
        hrActionService.saveHrActions(events);
        log.info("Finished processing HR action events.");
    }
}
