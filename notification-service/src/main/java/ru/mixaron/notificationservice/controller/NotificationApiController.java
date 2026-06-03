package ru.mixaron.notificationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mixaron.notificationservice.dto.NotificationMessage;
import ru.mixaron.notificationservice.service.NotificationStorageService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {

    @Autowired
    private NotificationStorageService storageService;

    @GetMapping("/recent")
    public List<NotificationMessage> getRecentMessages() {
        return storageService.getRecentMessages();
    }
}