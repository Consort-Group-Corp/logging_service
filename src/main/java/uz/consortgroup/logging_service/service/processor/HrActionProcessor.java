package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.entity.enumeration.HrActionType;
import uz.consortgroup.logging_service.service.HrActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HrActionProcessor implements ActionProcessor<HrActionEvent, HrActionType> {
    private final HrActionService hrActionService;

    @Override
    public boolean canHandle(HrActionType actionType) {
        return true;
    }

    @Override
    public void process(List<HrActionEvent> events) {
        hrActionService.saveHrActions(events);
    }
}
