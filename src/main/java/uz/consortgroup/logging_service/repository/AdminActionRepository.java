package uz.consortgroup.logging_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.logging_service.entity.SuperAdminAction;

import java.util.UUID;

@Repository
public interface AdminActionRepository extends JpaRepository<SuperAdminAction, UUID> {
}
