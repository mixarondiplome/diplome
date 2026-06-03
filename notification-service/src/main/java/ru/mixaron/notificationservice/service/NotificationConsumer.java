package ru.mixaron.notificationservice.service;

import com.example.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationStorageService notificationStorageService;

    @KafkaListener(topics = "transaction-events", groupId = "notification-group")
    public void listen(TransactionEvent event) {
        System.out.println("Transaction: " + event.toString());
        notificationStorageService.addMessage(event);
    }
}
