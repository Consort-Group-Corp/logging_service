package uz.consortgroup.logging_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.logging_service.entity.enumeration.MentorActionType;
import uz.consortgroup.logging_service.event.mentor.MentorActionEvent;
import uz.consortgroup.logging_service.service.MentorActionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MentorActionProcessor implements ActionProcessor<MentorActionEvent, MentorActionType>{
    private final MentorActionService mentorActionService;

    @Override
    public boolean canHandle(MentorActionType actionType) {
        return false;
    }

    @Override
    public void process(List<MentorActionEvent> event) {
        mentorActionService.saveMentorActions(event);
    }
}
