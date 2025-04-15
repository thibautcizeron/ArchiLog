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
            // Étape 1 : Récupérer la carte existante
            ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                    cardServiceUrl + "/" + listing.getCardId(), Map.class);

            if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                return false;
            }

            Map<String, Object> cardData = getResponse.getBody();
            cardData.put("userId", buyerId); // Mettre à jour le propriétaire de la carte

            // Étape 2 : Envoyer la mise à jour avec tous les champs
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(cardData);
            ResponseEntity<Void> updateResponse = restTemplate.exchange(
                    cardServiceUrl + "/" + listing.getCardId(),
                    HttpMethod.PUT,
                    request,
                    Void.class
            );

            if (!updateResponse.getStatusCode().is2xxSuccessful()) return false;

            // Étape 3 : Supprimer l'annonce du marché
            marketRepo.delete(listing);

            // Étape 4 : Enregistrer la transaction
            transactionRepo.save(Transaction.builder()
                    .cardId(cardId)
                    .buyerId(buyerId)
                    .sellerId(listing.getSellerId())
                    .price(listing.getPrice())
                    .timestamp(LocalDateTime.now())
                    .build());

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la communication avec cardService: " + e.getMessage());
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