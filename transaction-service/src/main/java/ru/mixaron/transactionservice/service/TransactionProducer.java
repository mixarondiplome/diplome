package ru.mixaron.transactionservice.service;

import com.example.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionProducer {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private long count = 1L;
    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Scheduled(fixedRate = 60000)
    public void sendPeriodicTransaction() {
        TransactionEvent event = new TransactionEvent(
                count++,
                1L,
                100.0,
                "USD",
                Instant.now()
        );
        kafkaTemplate.send("transaction-events", Long.toString(event.getId()), event);
        System.out.println("Sent transaction event: " + event);
    }
}
