package com.example.marketService.repository;

import com.example.marketService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBuyerIdOrSellerId(UUID buyerId, UUID sellerId);
}
