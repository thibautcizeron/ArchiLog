package com.example.cardService.controller;

import com.example.cardService.dto.CardDTO;
import com.example.cardService.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public List<CardDTO> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/available")
    public List<CardDTO> getAvailableCards() {
        return cardService.getAvailableCards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable UUID id) {
        return cardService.getCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CardDTO> createCard(@RequestBody CardDTO cardDTO) {
        return ResponseEntity.ok(cardService.createCard(cardDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDTO> updateCard(@PathVariable UUID id, @RequestBody CardDTO cardDTO) {
        return cardService.updateCard(id, cardDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        return cardService.deleteCard(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public List<CardDTO> getCardsByUser(@PathVariable UUID userId) {
        return cardService.getCardsByUserId(userId);
    }

    @GetMapping("/search")
    public List<CardDTO> searchCards(@RequestParam String keyword) {
        return cardService.searchCards(keyword);
    }

    @GetMapping("/filter")
    public List<CardDTO> filterCards(@RequestParam String rarity, @RequestParam String type) {
        return cardService.filterCards(rarity, type);
    }

    @GetMapping("/sort")
    public List<CardDTO> sortCards(@RequestParam String sortBy, @RequestParam(defaultValue = "true") boolean ascending) {
        return cardService.sortCards(sortBy, ascending);
    }
}
