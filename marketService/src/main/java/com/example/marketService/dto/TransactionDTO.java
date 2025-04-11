package com.example.marketService.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(
        UUID transactionId,
        UUID cardId,
        UUID buyerId,
        UUID sellerId,
        int price,
        LocalDateTime timestamp
) {}
