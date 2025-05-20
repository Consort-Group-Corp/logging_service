package uz.consortgroup.logging_service.service.processor;

import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;

import java.util.List;

public interface ActionProcessor<T, A extends Enum<A>> {
    boolean canHandle(A actionType);
    void process(List<T> events);
}
