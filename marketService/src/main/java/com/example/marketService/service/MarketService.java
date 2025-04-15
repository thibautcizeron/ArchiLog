package com.example.marketService.service;

import com.example.marketService.dto.MarketDTO;
import com.example.marketService.dto.TransactionDTO;
import com.example.marketService.model.Market;
import com.example.marketService.model.Transaction;
import com.example.marketService.repository.MarketRepository;
import com.example.marketService.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepo;
    private final TransactionRepository transactionRepo;
    private final RestTemplate restTemplate;

    private final String cardServiceUrl = "http://localhost:80/card/api/cards";

    public List<MarketDTO> getAllListings() {
        return marketRepo.findAll().stream()
                .map(c -> new MarketDTO(c.getCardId(), c.getSellerId(), c.getPrice()))
                .toList();
    }

    public boolean sellCard(MarketDTO marketDTO) {
        Optional<Market> existing = marketRepo.findByCardId(marketDTO.cardId());
        if (existing.isPresent()) return false;

        try {
            // Étape 1 : Récupérer la carte existante
            ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                    cardServiceUrl + "/" + marketDTO.cardId(), Map.class);

            if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                return false;
            }

            Map<String, Object> cardData = getResponse.getBody();
            cardData.put("userId", null); // Mettre userId à null

            // Étape 2 : Envoyer la mise à jour
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(cardData);
            ResponseEntity<Void> updateResponse = restTemplate.exchange(
                    cardServiceUrl + "/" + marketDTO.cardId(),
                    HttpMethod.PUT,
                    request,
                    Void.class
            );

            if (!updateResponse.getStatusCode().is2xxSuccessful()) return false;

            // Étape 3 : Sauvegarder l'annonce
            Market listing = Market.builder()
                    .cardId(marketDTO.cardId())
                    .sellerId(marketDTO.sellerId())
                    .price(marketDTO.price())
                    .build();

            marketRepo.save(listing);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la communication avec cardService: " + e.getMessage());
            return false;
        }
    }

    public boolean buyCard(UUID cardId, UUID buyerId) {
        Optional<Market> listingOpt = marketRepo.findByCardId(cardId);
        if (listingOpt.isEmpty()) return false;

        Market listing = listingOpt.get();
        
        try {
            // First check buyer's balance
            ResponseEntity<Map> userResponse = restTemplate.getForEntity(
                    "http://nginx/user/api/users/" + buyerId, 
                    Map.class);
            
            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                return false;
            }
            
            Map<String, Object> userData = userResponse.getBody();
            int currentBalance = (int) userData.get("solde");
            
            if (currentBalance < listing.getPrice()) {
                return false; // Not enough funds
            }
            
            // Update user balance
            userData.put("solde", currentBalance - listing.getPrice());
            
            HttpEntity<Map<String, Object>> userUpdateRequest = new HttpEntity<>(userData);
            ResponseEntity<Void> userUpdateResponse = restTemplate.exchange(
                    "http://nginx/user/api/users/" + buyerId,
                    HttpMethod.PUT,
                    userUpdateRequest,
                    Void.class
            );
            
            if (!userUpdateResponse.getStatusCode().is2xxSuccessful()) return false;
            
            // Update card ownership
            ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                    "http://nginx/card/api/cards/" + listing.getCardId(), 
                    Map.class);
            
            if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                return false;
            }
            
            Map<String, Object> cardData = getResponse.getBody();
            cardData.put("userId", buyerId); // Update card ownership
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(cardData);
            ResponseEntity<Void> updateResponse = restTemplate.exchange(
                    "http://nginx/card/api/cards/" + listing.getCardId(),
                    HttpMethod.PUT,
                    request,
                    Void.class
            );
            
            if (!updateResponse.getStatusCode().is2xxSuccessful()) return false;
            
            // Update seller's balance
            ResponseEntity<Map> sellerResponse = restTemplate.getForEntity(
                    "http://nginx/user/api/users/" + listing.getSellerId(), 
                    Map.class);
                    
            if (sellerResponse.getStatusCode().is2xxSuccessful() && sellerResponse.getBody() != null) {
                Map<String, Object> sellerData = sellerResponse.getBody();
                int sellerBalance = (int) sellerData.get("solde");
                sellerData.put("solde", sellerBalance + listing.getPrice());
                
                HttpEntity<Map<String, Object>> sellerUpdateRequest = new HttpEntity<>(sellerData);
                restTemplate.exchange(
                        "http://nginx/user/api/users/" + listing.getSellerId(),
                        HttpMethod.PUT,
                        sellerUpdateRequest,
                        Void.class
                );
            }

            // Remove the market listing
            marketRepo.delete(listing);
            
            // Record the transaction
            transactionRepo.save(Transaction.builder()
                    .cardId(cardId)
                    .buyerId(buyerId)
                    .sellerId(listing.getSellerId())
                    .price(listing.getPrice())
                    .timestamp(LocalDateTime.now())
                    .build());
                    
            return true;
        } catch (Exception e) {
            System.err.println("Error processing purchase: " + e.getMessage());
            return false;
        }
    }


    public List<TransactionDTO> getUserTransactions(UUID userId) {
        return transactionRepo.findByBuyerIdOrSellerId(userId, userId)
                .stream()
                .map(t -> new TransactionDTO(
                        t.getTransactionId(),
                        t.getCardId(),
                        t.getBuyerId(),
                        t.getSellerId(),
                        t.getPrice(),
                        t.getTimestamp()))
                .toList();
    }

    public void clearAllTransactions() {
        transactionRepo.deleteAll();
    }
}