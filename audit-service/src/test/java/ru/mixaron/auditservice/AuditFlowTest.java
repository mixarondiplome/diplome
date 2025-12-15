package ru.mixaron.auditservice;

import com.example.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ru.mixaron.auditservice.repository.AuditRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@RequiredArgsConstructor
public class AuditFlowTest extends AuditServiceIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AuditRepository auditRepository;

    private TransactionEvent createTransactionEvent(long eventId, long userId) {
        return  TransactionEvent.newBuilder()
                .setId(eventId)
                .setUserId(userId)
                .setAmount(100.0)
                .setCurrency("USD")
                .setTimestamp(Instant.now())
                .build();
    }

    @Test
    void shouldSaveTransactionToDatabase() {
        TransactionEvent event = createTransactionEvent(1L, 1L);

        kafkaTemplate.send("transaction-events", event);
        await()
                .pollInterval(Duration.ofSeconds(1))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var records = auditRepository.findAll();
                    assertEquals(1, records.size());
                    assertEquals(1L, records.getFirst().getUserId());
                });
    }
}
