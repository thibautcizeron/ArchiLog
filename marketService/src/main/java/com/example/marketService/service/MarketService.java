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
    private final String userServiceUrl = "http://nginx/user/api/users";

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
            Transaction transaction = transactionRepo.save(Transaction.builder()
                    .cardId(cardId)
                    .buyerId(buyerId)
                    .sellerId(listing.getSellerId())
                    .price(listing.getPrice())
                    .timestamp(LocalDateTime.now())
                    .build());

            // Étape 5 : Mettre à jour les soldes des utilisateurs
            updateUserBalances(buyerId, listing.getSellerId(), listing.getPrice());

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
    
    private void updateUserBalances(UUID buyerId, UUID sellerId, int price) {
        System.out.println("Mise à jour des soldes - Acheteur: " + buyerId + ", Vendeur: " + sellerId + ", Prix: " + price);
        
        try {
            // Récupérer le solde de l'acheteur
            ResponseEntity<Map> buyerResponse = restTemplate.getForEntity(
                    userServiceUrl + "/" + buyerId,
                    Map.class);
            
            System.out.println("Réponse acheteur: " + buyerResponse.getStatusCode());
            
            if (buyerResponse.getStatusCode().is2xxSuccessful() && buyerResponse.getBody() != null) {
                Map<String, Object> buyerData = buyerResponse.getBody();
                System.out.println("Données acheteur: " + buyerData);
                
                // Vérifier si solde est un nombre ou une chaîne de caractères
                int buyerSolde = 0;
                Object soldeObj = buyerData.get("solde");
                
                if (soldeObj instanceof Integer) {
                    buyerSolde = (Integer) soldeObj;
                } else if (soldeObj instanceof Double) {
                    buyerSolde = ((Double) soldeObj).intValue();
                } else if (soldeObj instanceof String) {
                    buyerSolde = Integer.parseInt((String) soldeObj);
                }
                
                System.out.println("Solde actuel acheteur: " + buyerSolde);
                
                // Mettre à jour le solde de l'acheteur (déduire le prix)
                int newBuyerSolde = buyerSolde - price;
                buyerData.put("solde", newBuyerSolde);
                System.out.println("Nouveau solde acheteur: " + newBuyerSolde);
                
                HttpEntity<Map<String, Object>> buyerRequest = new HttpEntity<>(buyerData);
                ResponseEntity<Void> buyerUpdateResponse = restTemplate.exchange(
                        userServiceUrl + "/" + buyerId,
                        HttpMethod.PUT,
                        buyerRequest,
                        Void.class
                );
                
                System.out.println("Mise à jour acheteur: " + buyerUpdateResponse.getStatusCode());
            }
            
            if (sellerId != null) {
                // Récupérer le solde du vendeur
                ResponseEntity<Map> sellerResponse = restTemplate.getForEntity(
                        userServiceUrl + "/" + sellerId,
                        Map.class);
                
                System.out.println("Réponse vendeur: " + sellerResponse.getStatusCode());
                
                if (sellerResponse.getStatusCode().is2xxSuccessful() && sellerResponse.getBody() != null) {
                    Map<String, Object> sellerData = sellerResponse.getBody();
                    System.out.println("Données vendeur: " + sellerData);
                    
                    // Vérifier si solde est un nombre ou une chaîne de caractères
                    int sellerSolde = 0;
                    Object soldeObj = sellerData.get("solde");
                    
                    if (soldeObj instanceof Integer) {
                        sellerSolde = (Integer) soldeObj;
                    } else if (soldeObj instanceof Double) {
                        sellerSolde = ((Double) soldeObj).intValue();
                    } else if (soldeObj instanceof String) {
                        sellerSolde = Integer.parseInt((String) soldeObj);
                    }
                    
                    System.out.println("Solde actuel vendeur: " + sellerSolde);
                    
                    // Mettre à jour le solde du vendeur (ajouter le prix)
                    int newSellerSolde = sellerSolde + price;
                    sellerData.put("solde", newSellerSolde);
                    System.out.println("Nouveau solde vendeur: " + newSellerSolde);
                    
                    HttpEntity<Map<String, Object>> sellerRequest = new HttpEntity<>(sellerData);
                    ResponseEntity<Void> sellerUpdateResponse = restTemplate.exchange(
                            userServiceUrl + "/" + sellerId,
                            HttpMethod.PUT,
                            sellerRequest,
                            Void.class
                    );
                    
                    System.out.println("Mise à jour vendeur: " + sellerUpdateResponse.getStatusCode());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des soldes : " + e.getMessage());
            e.printStackTrace();
        }
    }
}