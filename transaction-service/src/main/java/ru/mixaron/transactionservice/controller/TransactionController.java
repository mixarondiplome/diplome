package ru.mixaron.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mixaron.transactionservice.dto.TransactionDTO;
import ru.mixaron.transactionservice.service.TransactionProducer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionProducer producerService;
    private long count = 1L;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendTransaction(@RequestBody TransactionDTO request) {
        producerService.sendPeriodicTransaction(request);

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionId", count++);
        transaction.put("userId", request.getUserId());
        transaction.put("amount", request.getAmount());
        transaction.put("currency", request.getCurrency());
        transaction.put("timestamp", Instant.now().toEpochMilli());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Транзакция успешно отправлена");
        response.put("transaction", transaction);

        return ResponseEntity.ok(response);
    }
}
