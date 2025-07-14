package uz.consortgroup.logging_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.logging_service.entity.enumeration.MentorActionType;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.service.MentorActionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorActionProcessorTest {

    @Mock
    private MentorActionService mentorActionService;

    @InjectMocks
    private MentorActionProcessor mentorActionProcessor;

    @Test
    void canHandleShouldAlwaysReturnFalse() {
        for (MentorActionType type : MentorActionType.values()) {
            assertFalse(mentorActionProcessor.canHandle(type));
        }
    }

    @Test
    void processShouldDelegateToMentorActionService() {
        List<MentorActionEvent> events = List.of(
            new MentorActionEvent(),
            new MentorActionEvent()
        );

        mentorActionProcessor.process(events);

        verify(mentorActionService).saveMentorActions(events);
    }

    @Test
    void processShouldHandleEmptyList() {
        List<MentorActionEvent> emptyList = List.of();

        mentorActionProcessor.process(emptyList);

        verify(mentorActionService).saveMentorActions(emptyList);
    }
}