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

    private final String cardServiceUrl = "http://nginx/card/api/cards";
    private final String userServiceUrl = "http://nginx/user/api/users";

    /**
     * Récupère toutes les annonces du marché avec les détails des cartes
     */
    public List<MarketDTO> getAllListings() {
        List<MarketDTO> enrichedListings = new ArrayList<>();
        
        for (Market listing : marketRepo.findAll()) {
            UUID cardId = listing.getCardId();
            UUID sellerId = listing.getSellerId();
            int price = listing.getPrice();
            
            // Enrichir avec les données de la carte si nécessaire
            Map<String, Object> cardData = getCardData(cardId);
            // Utiliser uniquement les informations sûres et nécessaires
            
            enrichedListings.add(new MarketDTO(cardId, sellerId, price));
        }
        
        return enrichedListings;
    }

    /**
     * Met une carte en vente sur le marché
     * Vérifie que l'utilisateur est bien propriétaire de la carte
     */
    public String sellCard(UUID cardId) {
        try {
            // Récupérer les détails de la carte
            Map<String, Object> cardData = getCardData(cardId);
            if (cardData == null) return "Carte non trouvée.";
    
            // Vérifier si la carte a un propriétaire
            String userId = (String) cardData.get("userId");
            if (userId == null) return "Carte non possédée.";
    
            // Récupérer les détails de l'utilisateur
            Map<String, Object> userData = getUserData(UUID.fromString(userId));
            if (userData == null) return "Utilisateur non trouvé.";
    
            // Mettre à jour la carte pour la mettre en vente (retirer le propriétaire)
            cardData.put("userId", null); // La carte est maintenant en vente
    
            // Enregistrer l'annonce sur le marché
            Market listing = Market.builder()
                    .cardId(cardId)
                    .sellerId(UUID.fromString(userId))
                    .price(extractCardPrice(cardData)) // Prix à définir ici
                    .build();
            marketRepo.save(listing);
    
            // Sauvegarder les modifications
            updateCard(cardId, cardData);
    
            return "Carte mise en vente avec succès.";
        } catch (Exception e) {
            return "Erreur lors de la mise en vente : " + e.getMessage();
        }
    }
    

    /**
     * Permet à un utilisateur d'acheter une carte
     * Vérifie la disponibilité et gère les transactions financières
     */
    public boolean buyCard(UUID cardId, UUID buyerId) {
        if (cardId == null || buyerId == null) {
            return false;
        }
    
        // Vérifier le solde de l'utilisateur avant tout
        Map<String, Object> buyerData = getUserData(buyerId);
        if (buyerData == null) {
            return false;
        }
    
        int buyerBalance = extractUserBalance(buyerData);
    
        // Vérifier si la carte est dans le marché
        Optional<Market> marketListing = marketRepo.findByCardId(cardId);
        UUID sellerId = null;
        int price = 0;
    
        try {
            // Récupérer les données de la carte
            Map<String, Object> cardData = getCardData(cardId);
            if (cardData == null) return false;
    
            // Traiter différemment selon si c'est une carte du marché ou non
            if (marketListing.isPresent()) {
                // Achat via marché
                Market listing = marketListing.get();
                sellerId = listing.getSellerId();
                price = listing.getPrice();
    
                // Vérifier que l'acheteur a assez d'argent
                if (buyerBalance < price) {
                    return false;
                }
    
                // Effectuer la transaction (mettre à jour les soldes)
                updateUserBalances(buyerId, sellerId, price);
    
                // Mettre à jour la carte : changer le propriétaire (acheteur B)
                cardData.put("userId", buyerId.toString());
                updateCard(cardId, cardData);
    
                // Supprimer l'annonce du marché, car la carte a été achetée
                marketRepo.delete(marketListing.get());
            } else {
                // Achat direct (carte sans propriétaire)
                if (cardData.get("userId") != null) {
                    return false; // La carte a un propriétaire, impossible d'acheter
                }
    
                // Utiliser le prix de base pour l'achat direct
                price = extractCardPrice(cardData);
    
                // Vérifier que l'acheteur a assez d'argent
                if (buyerBalance < price) {
                    return false;
                }
    
                // Mettre à jour la carte et l'utilisateur
                cardData.put("userId", buyerId.toString());
                updateCard(cardId, cardData);
    
                // Déduire l'argent du compte de l'acheteur
                buyerData.put("solde", buyerBalance - price);
                updateUserData(buyerId, buyerData);
            }
    
            // Enregistrer la transaction
            transactionRepo.save(Transaction.builder()
                    .cardId(cardId)
                    .buyerId(buyerId)
                    .sellerId(sellerId)
                    .price(price)
                    .timestamp(LocalDateTime.now())
                    .build());
    
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    

    /**
     * Récupère l'historique des transactions d'un utilisateur
     */
    public List<TransactionDTO> getUserTransactions(UUID userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
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

    /**
     * Supprime toutes les transactions (fonction administrative)
     */
    public void clearAllTransactions() {
        transactionRepo.deleteAll();
    }
    
    /**
     * Récupère les données d'une carte depuis le service de cartes
     */
    private Map<String, Object> getCardData(UUID cardId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    cardServiceUrl + "/" + cardId, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Récupère les données d'un utilisateur depuis le service utilisateur
     */
    private Map<String, Object> getUserData(UUID userId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    userServiceUrl + "/" + userId, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Met à jour les données d'une carte
     */
    private boolean updateCard(UUID cardId, Map<String, Object> cardData) {
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(cardData);
            ResponseEntity<Void> response = restTemplate.exchange(
                    cardServiceUrl + "/" + cardId,
                    HttpMethod.PUT,
                    request,
                    Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Met à jour les données d'un utilisateur
     */
    private boolean updateUserData(UUID userId, Map<String, Object> userData) {
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData);
            ResponseEntity<Void> response = restTemplate.exchange(
                    userServiceUrl + "/" + userId,
                    HttpMethod.PUT,
                    request,
                    Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extrait le prix d'une carte de manière sécurisée
     */
    private int extractCardPrice(Map<String, Object> cardData) {
        if (cardData == null || !cardData.containsKey("price")) return 0;
        Object price = cardData.get("price");
        
        if (price instanceof Integer) return (Integer) price;
        if (price instanceof Double) return ((Double) price).intValue();
        if (price instanceof String) {
            try {
                return Integer.parseInt((String) price);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Extrait le solde d'un utilisateur de manière sécurisée
     */
    private int extractUserBalance(Map<String, Object> userData) {
        if (userData == null || !userData.containsKey("solde")) return 0;
        Object solde = userData.get("solde");
        
        if (solde instanceof Integer) return (Integer) solde;
        if (solde instanceof Double) return ((Double) solde).intValue();
        if (solde instanceof String) {
            try {
                return Integer.parseInt((String) solde);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Met à jour les soldes des utilisateurs lors d'une transaction
     */
    private void updateUserBalances(UUID buyerId, UUID sellerId, int price) {
        try {
            // Mettre à jour le solde de l'acheteur
            Map<String, Object> buyerData = getUserData(buyerId);
            if (buyerData != null) {
                int buyerSolde = extractUserBalance(buyerData);
                int newBuyerSolde = buyerSolde - price;
                buyerData.put("solde", newBuyerSolde);
                updateUserData(buyerId, buyerData);
            }
            
            // Mettre à jour le solde du vendeur
            if (sellerId != null) {
                Map<String, Object> sellerData = getUserData(sellerId);
                if (sellerData != null) {
                    int sellerSolde = extractUserBalance(sellerData);
                    int newSellerSolde = sellerSolde + price;
                    sellerData.put("solde", newSellerSolde);
                    updateUserData(sellerId, sellerData);
                }
            }
        } catch (Exception e) {
            // Enregistrer l'erreur mais ne pas interrompre le processus
        }
    }
}