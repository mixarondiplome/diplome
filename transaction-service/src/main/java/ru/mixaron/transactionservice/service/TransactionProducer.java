package ru.mixaron.transactionservice.service;

import com.example.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mixaron.transactionservice.dto.TransactionDTO;

import java.time.Instant;

@Service
public class TransactionProducer {

    private final static Logger logger = LoggerFactory.getLogger(TransactionProducer.class);
    private final static String TOPIC = "transaction-events";
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private long count = 1L;

    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPeriodicTransaction(TransactionDTO request) {
        TransactionEvent event = TransactionEvent.newBuilder()
                .setId(count++)
                .setUserId(request.getUserId())
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency())
                .setTimestamp(Instant.now())
                .build();
        kafkaTemplate.send(TOPIC, Long.toString(event.getId()), event);
        logger.info("Sent transaction event: {}", event);
    }
}
