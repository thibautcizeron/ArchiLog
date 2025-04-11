package com.example.cardService.repository;

import com.example.cardService.model.Card;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByUserId(UUID userId);

    @Query("SELECT c FROM Card c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Card> searchByNameOrDescription(String keyword);

    List<Card> findByRarityAndType(String rarity, String type);

    List<Card> findAll(Sort sort);
}
