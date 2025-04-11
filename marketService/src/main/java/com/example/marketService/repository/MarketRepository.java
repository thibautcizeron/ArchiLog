package com.example.marketService.repository;

import com.example.marketService.model.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarketRepository extends JpaRepository<Market, UUID> {
    Optional<Market> findByCardId(UUID cardId);
}
