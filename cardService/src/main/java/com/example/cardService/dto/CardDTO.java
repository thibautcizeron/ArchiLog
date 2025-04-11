package com.example.cardService.dto;

import java.util.UUID;

public record CardDTO(
        UUID id,
        String name,
        String description,
        String rarity,
        String type,
        String imageUrl,
        UUID userId
) {}
