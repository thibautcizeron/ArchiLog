package com.example.marketService.controller;

import com.example.marketService.dto.MarketDTO;
import com.example.marketService.dto.TransactionDTO;
import com.example.marketService.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping
    public ResponseEntity<List<MarketDTO>> getAllListings() {
        return ResponseEntity.ok(marketService.getAllListings());
    }

    // Dans le contrôleur
    @PostMapping("/sell/{cardId}")
    public ResponseEntity<String> sellCard(@PathVariable UUID cardId) {
        String result = marketService.sellCard(cardId);
        
        if (result.contains("succès")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping("/buy/{cardId}")
    public ResponseEntity<Void> buyCard(@PathVariable UUID cardId, @RequestParam UUID buyerId) {
        boolean success = marketService.buyCard(cardId, buyerId);
        return success
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable UUID userId) {
        return ResponseEntity.ok(marketService.getUserTransactions(userId));
    }

    @DeleteMapping("/transactions/clear")
    public ResponseEntity<Void> clearAllTransactions() {
        marketService.clearAllTransactions();
        return ResponseEntity.ok().build();
    }
}