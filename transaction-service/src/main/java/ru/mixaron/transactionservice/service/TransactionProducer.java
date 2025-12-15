package ru.mixaron.transactionservice.service;

import com.example.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionProducer {

    private final static Logger logger = LoggerFactory.getLogger(TransactionProducer.class);

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private long count = 1L;
    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Scheduled(fixedRate = 60000)
    public void sendPeriodicTransaction() {
        TransactionEvent event = TransactionEvent.newBuilder()
                .setId(count++)
                .setUserId(1L)
                .setAmount(100.0)
                .setCurrency("USD")
                .setTimestamp(Instant.now())
                .build();
        kafkaTemplate.send("transaction-events", Long.toString(event.getId()), event);
        logger.info("Sent transaction event: {}", event);
    }
}
