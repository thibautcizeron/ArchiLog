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

    // Correction ici pour utiliser nginx comme proxy inverse
    private final String cardServiceUrl = "http://nginx/card/api/cards";

    public List<MarketDTO> getAllListings() {
        return marketRepo.findAll().stream()
                .map(c -> new MarketDTO(c.getCardId(), c.getSellerId(), c.getPrice()))
                .toList();
    }

    public boolean sellCard(MarketDTO marketDTO) {
        try {
            // Vérifier si la carte est déjà dans le marché
            Optional<Market> existing = marketRepo.findByCardId(marketDTO.cardId());
            if (existing.isPresent()) {
                System.out.println("Carte déjà dans le marché: " + marketDTO.cardId());
                return false;
            }

            // Étape 1 : Récupérer la carte existante
            System.out.println("Récupération de la carte: " + marketDTO.cardId());
            ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                    cardServiceUrl + "/" + marketDTO.cardId(), Map.class);

            if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                System.out.println("Échec de la récupération de la carte: " + getResponse.getStatusCode());
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

            if (!updateResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("Échec de la mise à jour de la carte: " + updateResponse.getStatusCode());
                return false;
            }

            // Étape 3 : Sauvegarder l'annonce
            Market listing = Market.builder()
                    .cardId(marketDTO.cardId())
                    .sellerId(marketDTO.sellerId())
                    .price(marketDTO.price())
                    .build();

            marketRepo.save(listing);
            System.out.println("Mise en vente réussie pour la carte: " + marketDTO.cardId());
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise en vente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean buyCard(UUID cardId, UUID buyerId) {
        System.out.println("Tentative d'achat - cardId: " + cardId + ", buyerId: " + buyerId);
        
        // Vérifier si la carte est dans le marché
        Optional<Market> listingOpt = marketRepo.findByCardId(cardId);
        System.out.println("Carte trouvée dans le marché: " + listingOpt.isPresent());
        
        if (listingOpt.isEmpty()) {
            // Si la carte n'est pas dans le marché, essayons quand même de mettre à jour le propriétaire
            // Cela permet d'acheter directement une carte sans passer par le marché pour les tests
            try {
                // Récupérer directement la carte
                ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                        cardServiceUrl + "/" + cardId, Map.class);

                if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                    System.out.println("Échec de la récupération de la carte (hors marché): " + getResponse.getStatusCode());
                    return false;
                }

                Map<String, Object> cardData = getResponse.getBody();
                
                // Vérifier si la carte est déjà possédée
                if (cardData.get("userId") != null) {
                    System.out.println("La carte est déjà possédée par un utilisateur: " + cardData.get("userId"));
                    return false;
                }
                
                // Mettre à jour le propriétaire
                cardData.put("userId", buyerId.toString());

                // Envoyer la mise à jour
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(cardData);
                ResponseEntity<Void> updateResponse = restTemplate.exchange(
                        cardServiceUrl + "/" + cardId,
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

                if (!updateResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Échec de la mise à jour du propriétaire (hors marché): " + updateResponse.getStatusCode());
                    return false;
                }

                // Enregistrer une transaction directe
                int price = cardData.containsKey("price") ? 
                        (cardData.get("price") instanceof Integer ? (Integer)cardData.get("price") : 0) : 0;
                
                transactionRepo.save(Transaction.builder()
                        .cardId(cardId)
                        .buyerId(buyerId)
                        .sellerId(null) // Pas de vendeur pour un achat direct
                        .price(price)
                        .timestamp(LocalDateTime.now())
                        .build());
                
                System.out.println("Achat direct réussi pour la carte: " + cardId);
                return true;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'achat direct: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        // Procéder à l'achat normal via le marché
        Market listing = listingOpt.get();

        try {
            // Étape 1 : Récupérer la carte existante
            ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                    cardServiceUrl + "/" + listing.getCardId(), Map.class);

            if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                System.out.println("Échec de la récupération de la carte: " + getResponse.getStatusCode());
                return false;
            }

            Map<String, Object> cardData = getResponse.getBody();
            cardData.put("userId", buyerId.toString()); // Mettre à jour le propriétaire

            // Étape 2 : Envoyer la mise à jour avec tous les champs
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(cardData);
            ResponseEntity<Void> updateResponse = restTemplate.exchange(
                    cardServiceUrl + "/" + listing.getCardId(),
                    HttpMethod.PUT,
                    request,
                    Void.class
            );

            if (!updateResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("Échec de la mise à jour du propriétaire: " + updateResponse.getStatusCode());
                return false;
            }

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

            System.out.println("Achat réussi pour la carte: " + cardId);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'achat: " + e.getMessage());
            e.printStackTrace();
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