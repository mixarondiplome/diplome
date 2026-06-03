package ru.mixaron.notificationservice.dto;

public record NotificationMessage(long timestamp, Long transactionId, Long userId, Double amount, String currency) {
}
