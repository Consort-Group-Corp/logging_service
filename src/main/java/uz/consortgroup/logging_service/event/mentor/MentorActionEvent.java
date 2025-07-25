package uz.consortgroup.logging_service.event.mentor;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.logging_service.entity.enumeration.MentorActionType;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MentorActionEvent {
    private UUID messageId;
    private UUID mentorId;
    private UUID resourceId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private MentorActionType mentorActionType;
    private LocalDateTime createdAt;
}
