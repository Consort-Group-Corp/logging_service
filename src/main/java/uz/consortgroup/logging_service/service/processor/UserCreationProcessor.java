package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.entity.ActionType;
import uz.consortgroup.logging_service.event.admin.UserCreatedEvent;
import uz.consortgroup.logging_service.service.AdminActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserCreationProcessor implements ActionProcessor<UserCreatedEvent> {
    private final AdminActionService adminActionService;

    @Override
    public boolean canHandle(ActionType actionType) {
        return actionType == ActionType.USER_CREATED;
    }

    @Override
    public void process(List<UserCreatedEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        adminActionService.saveAdminActions(events);
    }
}
