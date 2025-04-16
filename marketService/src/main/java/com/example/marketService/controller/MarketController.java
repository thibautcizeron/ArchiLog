package com.example.marketService.controller;

import com.example.marketService.dto.MarketDTO;
import com.example.marketService.dto.TransactionDTO;
import com.example.marketService.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping
    public List<MarketDTO> getAllListings() {
        return marketService.getAllListings();
    }

    @PostMapping("/sell")
    public ResponseEntity<Void> sellCard(@RequestBody MarketDTO marketDTO) {
        return marketService.sellCard(marketDTO)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/buy/{cardId}")
    public ResponseEntity<Void> buyCard(@PathVariable UUID cardId, @RequestParam UUID buyerId) {
        boolean success = marketService.buyCard(cardId, buyerId);
        System.out.println("Résultat de l'achat - cardId: " + cardId + ", success: " + success);
        return success
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/transactions/{userId}")
    public List<TransactionDTO> getUserTransactions(@PathVariable UUID userId) {
        return marketService.getUserTransactions(userId);
    }


    @DeleteMapping("/transactions/clear")
    public ResponseEntity<Void> clearAllTransactions() {
        marketService.clearAllTransactions();
        return ResponseEntity.ok().build();
    }
}
