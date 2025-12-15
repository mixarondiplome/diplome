package ru.mixaron.auditservice.service;

import com.example.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.mixaron.auditservice.mapper.AuditMapper;
import ru.mixaron.auditservice.model.AuditLog;
import ru.mixaron.auditservice.repository.AuditRepository;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;
    private final AuditMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @KafkaListener(topics = "transaction-events", groupId = "audit-group")
    public void auditListener(TransactionEvent event) {
        try {
            saveAudit(mapper.toLog(event));
        } catch (DataIntegrityViolationException e) {
            logger.warn("Event ID already exists in audit_log");
        }
    }

    public void saveAudit(AuditLog log) {
        auditRepository.save(log);
        System.out.println("log successfully save");
    }
}
