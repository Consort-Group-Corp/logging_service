package uz.consortgroup.logging_service.service.processor;

import uz.consortgroup.logging_service.entity.ActionType;

import java.util.List;

public interface ActionProcessor<T> {
    boolean canHandle(ActionType actionType);
    void process(List<T> event);
}
