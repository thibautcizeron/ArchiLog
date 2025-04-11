package com.example.marketService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transactionId;

    private UUID cardId;
    private UUID buyerId;
    private UUID sellerId;
    private int price;
    private LocalDateTime timestamp;
}
