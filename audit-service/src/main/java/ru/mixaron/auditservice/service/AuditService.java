package ru.mixaron.auditservice.service;

import com.example.TransactionEvent;
import lombok.RequiredArgsConstructor;
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

    @KafkaListener(topics = "transaction-events", groupId = "audit-group")
    public void auditListener(TransactionEvent event) {
        saveAudit(mapper.toLog(event));
    }

    public void saveAudit(AuditLog log) {
        auditRepository.save(log);
        System.out.println("log successfully save");
    }
}
