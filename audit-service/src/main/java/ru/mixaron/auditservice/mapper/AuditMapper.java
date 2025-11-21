package ru.mixaron.auditservice.mapper;

import com.example.TransactionEvent;
import org.springframework.stereotype.Component;
import ru.mixaron.auditservice.model.AuditLog;

@Component
public class AuditMapper {
    public AuditLog toLog(TransactionEvent event) {
        AuditLog log = AuditLog.builder()
                .userId(event.getUserId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .createdAt(event.getTimestamp())
                .build();
        return log;
    }
}
