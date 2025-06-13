package uz.consortgroup.logging_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.logging_service.entity.HrAction;

import java.util.UUID;

@Repository
public interface HrActionRepository extends JpaRepository<HrAction, UUID> {
}
