package uz.consortgroup.logging_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.consortgroup.logging_service.entity.enumeration.HrActionType;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "hr_action", schema = "logging_schema")
public class HrAction {
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hr_id", nullable = false)
    private UUID hrId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "hr_action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HrActionType hrActionType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
