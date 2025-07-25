package uz.consortgroup.logging_service.event.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.logging_service.entity.enumeration.SuperAdminActionType;
import uz.consortgroup.logging_service.entity.enumeration.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SuperAdminActionEvent {
    private UUID messageId;
    private UUID adminId;
    private UUID userId;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UserRole role;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SuperAdminActionType superAdminActionType;
    private LocalDateTime createdAt;
}