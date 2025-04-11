package com.example.marketService.dto;

import java.util.UUID;

public record MarketDTO(
        UUID cardId,
        UUID sellerId,
        int price
) {}
