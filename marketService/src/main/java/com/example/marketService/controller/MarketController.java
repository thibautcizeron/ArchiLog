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

    @PostMapping("/buy")  
    public ResponseEntity<?> buyCard(@RequestBody Map<String, Object> request) {
        try {
            UUID cardId = UUID.fromString(request.get("cardId").toString());
            UUID buyerId = UUID.fromString(request.get("userId").toString());
            
            boolean success = marketService.buyCard(cardId, buyerId);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Purchase successful"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Failed to complete purchase"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request: " + e.getMessage()));
        }
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
