package ru.mixaron.notificationservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    @GetMapping("/")
    public String index() {
        return "notifications";
    }
}
