package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.event.admin.SuperAdminUserActionEvent;
import uz.consortgroup.logging_service.service.AdminActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SuperAdminActionProcessor implements ActionProcessor<SuperAdminUserActionEvent, SuperAdminActionType> {
    private final AdminActionService adminActionService;

    @Override
    public boolean canHandle(SuperAdminActionType superAdminActionType) {
        return superAdminActionType == SuperAdminActionType.USER_CREATED;
    }

    @Override
    public void process(List<SuperAdminUserActionEvent> events) {
        adminActionService.saveAdminActions(events);
    }
}
