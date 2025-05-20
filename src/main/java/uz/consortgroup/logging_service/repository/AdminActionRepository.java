package uz.consortgroup.logging_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.consortgroup.logging_service.entity.SuperAdminAction;

import java.util.UUID;

public interface AdminActionRepository extends JpaRepository<SuperAdminAction, UUID> {
}
