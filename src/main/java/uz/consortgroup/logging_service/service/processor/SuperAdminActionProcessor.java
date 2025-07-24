package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.event.admin.SuperAdminActionEvent;
import uz.consortgroup.logging_service.service.AdminActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SuperAdminActionProcessor implements ActionProcessor<SuperAdminActionEvent, SuperAdminActionType> {

    private final AdminActionService adminActionService;

    @Override
    public boolean canHandle(SuperAdminActionType superAdminActionType) {
        log.info("Checking if processor can handle action type: {}", superAdminActionType);
        return superAdminActionType == SuperAdminActionType.USER_CREATED;
    }

    @Override
    public void process(List<SuperAdminActionEvent> events) {
        if (events == null || events.isEmpty()) {
            log.warn("No SuperAdminActionEvents to process.");
            return;
        }

        log.info("Processing {} SuperAdminActionEvent(s).", events.size());
        adminActionService.saveAdminActions(events);
        log.info("Finished processing SuperAdminActionEvents.");
    }
}
