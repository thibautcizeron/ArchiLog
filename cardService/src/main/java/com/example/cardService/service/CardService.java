package com.example.cardService.service;

import com.example.cardService.dto.CardDTO;
import com.example.cardService.model.Card;
import com.example.cardService.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private CardDTO toDTO(Card card) {
        return new CardDTO(
                card.getId(),
                card.getName(),
                card.getDescription(),
                card.getRarity(),
                card.getType(),
                card.getImageUrl(),
                card.getUserId(),
                card.getPrice()
        );
    }

    private Card fromDTO(CardDTO dto) {
        return Card.builder()
                .name(dto.name())
                .description(dto.description())
                .rarity(dto.rarity())
                .type(dto.type())
                .imageUrl(dto.imageUrl())
                .userId(dto.userId())
                .price(dto.price())
                .build();
    }

    public List<CardDTO> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CardDTO> getCardById(UUID id) {
        return cardRepository.findById(id).map(this::toDTO);
    }

    public CardDTO createCard(CardDTO dto) {
        Card card = fromDTO(dto);
        return toDTO(cardRepository.save(card));
    }

    public Optional<CardDTO> updateCard(UUID id, CardDTO dto) {
        return cardRepository.findById(id).map(card -> {
            card.setName(dto.name());
            card.setDescription(dto.description());
            card.setRarity(dto.rarity());
            card.setType(dto.type());
            card.setImageUrl(dto.imageUrl());
            card.setUserId(dto.userId());
            card.setPrice(dto.price());
            return toDTO(cardRepository.save(card));
        });
    }

    public boolean deleteCard(UUID id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<CardDTO> getCardsByUserId(UUID userId) {
        return cardRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CardDTO> searchCards(String keyword) {
        return cardRepository.searchByNameOrDescription(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CardDTO> filterCards(String rarity, String type) {
        return cardRepository.findByRarityAndType(rarity, type).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CardDTO> sortCards(String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return cardRepository.findAll(sort).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CardDTO> getAvailableCards() {
        return cardRepository.findAll()
                .stream()
                .filter(card -> card.getUserId() == null)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}