package ru.mixaron.auditservice.mapper;

import com.example.TransactionEvent;
import org.springframework.stereotype.Component;
import ru.mixaron.auditservice.model.AuditLog;

@Component
public class AuditMapper {
    public AuditLog toLog(TransactionEvent event) {
        return AuditLog.builder()
                .userId(event.getUserId())
                .eventId(event.getId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .createdAt(event.getTimestamp())
                .build();
    }
}
