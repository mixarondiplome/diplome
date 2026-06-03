package ru.mixaron.transactionservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {
    private Long userId;
    private Double amount;
    private String currency;
}