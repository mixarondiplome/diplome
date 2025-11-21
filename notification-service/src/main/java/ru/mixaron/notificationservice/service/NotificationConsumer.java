package ru.mixaron.notificationservice.service;

import com.example.TransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "transaction-events", groupId = "notification-group")
    public void listen(TransactionEvent event) {
        System.out.println("Transaction: " + event.toString());
    }
}
