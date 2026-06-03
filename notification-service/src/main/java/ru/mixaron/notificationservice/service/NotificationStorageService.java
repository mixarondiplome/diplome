package ru.mixaron.notificationservice.service;

import com.example.TransactionEvent;
import org.springframework.stereotype.Service;
import ru.mixaron.notificationservice.dto.NotificationMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class NotificationStorageService {

    private final ConcurrentLinkedDeque<NotificationMessage> messages = new ConcurrentLinkedDeque<>();
    private final int MAX_SIZE = 50;

    public void addMessage(TransactionEvent event) {
        NotificationMessage msg = new NotificationMessage(
                System.currentTimeMillis(),
                event.getId(),
                event.getUserId(),
                event.getAmount(),
                event.getCurrency()
        );

        messages.addFirst(msg);

        while (messages.size() > MAX_SIZE) {
            messages.removeLast();
        }
    }

    public List<NotificationMessage> getRecentMessages() {
        return new LinkedList<>(messages);
    }
}