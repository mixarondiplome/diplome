package ru.mixaron.auditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mixaron.auditservice.model.AuditLog;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {
}
