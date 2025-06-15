package uz.consortgroup.logging_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.logging_service.entity.enumeration.HrActionType;
import uz.consortgroup.logging_service.event.hr.HrActionEvent;
import uz.consortgroup.logging_service.service.HrActionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HrActionProcessorTest {

    @Mock
    private HrActionService hrActionService;

    @InjectMocks
    private HrActionProcessor hrActionProcessor;

    @Test
    void canHandleShouldAlwaysReturnTrue() {
        for (HrActionType type : HrActionType.values()) {
            assertTrue(hrActionProcessor.canHandle(type));
        }
    }

    @Test
    void processShouldDelegateToHrActionService() {
        List<HrActionEvent> events = List.of(
            new HrActionEvent(),
            new HrActionEvent()
        );

        hrActionProcessor.process(events);

        verify(hrActionService).saveHrActions(events);
    }

    @Test
    void processShouldHandleEmptyList() {
        List<HrActionEvent> emptyList = List.of();

        hrActionProcessor.process(emptyList);

        verify(hrActionService).saveHrActions(emptyList);
    }
}