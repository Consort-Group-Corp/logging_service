package uz.consortgroup.logging_service.event.hr;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.logging_service.entity.enumeration.HrActionType;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HrActionEvent {
    private UUID messageId;
    private UUID hrId;
    private UUID userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private HrActionType hrActionType;
    private LocalDateTime createdAt;
    
}
