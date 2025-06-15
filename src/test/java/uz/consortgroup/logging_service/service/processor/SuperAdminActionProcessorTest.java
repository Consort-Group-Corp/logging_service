package uz.consortgroup.logging_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.event.admin.SuperAdminUserActionEvent;
import uz.consortgroup.logging_service.service.AdminActionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SuperAdminActionProcessorTest {

    @Mock
    private AdminActionService adminActionService;

    @InjectMocks
    private SuperAdminActionProcessor superAdminActionProcessor;

    @Test
    void canHandleShouldReturnTrueOnlyForUserCreated() {
        assertTrue(superAdminActionProcessor.canHandle(SuperAdminActionType.USER_CREATED));
        
        for (SuperAdminActionType type : SuperAdminActionType.values()) {
            if (type != SuperAdminActionType.USER_CREATED) {
                assertFalse(superAdminActionProcessor.canHandle(type));
            }
        }
    }

    @Test
    void processShouldDelegateToAdminActionService() {
        List<SuperAdminUserActionEvent> events = List.of(
            new SuperAdminUserActionEvent(),
            new SuperAdminUserActionEvent()
        );

        superAdminActionProcessor.process(events);

        verify(adminActionService).saveAdminActions(events);
    }

    @Test
    void processShouldHandleEmptyList() {
        List<SuperAdminUserActionEvent> emptyList = List.of();

        superAdminActionProcessor.process(emptyList);

        verify(adminActionService).saveAdminActions(emptyList);
    }
}