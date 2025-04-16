package com.example.marketService.dto;

import java.util.UUID;
import java.util.Objects;

public class MarketDTO {
    private final UUID cardId;
    private final UUID sellerId;
    private final int price;

    public MarketDTO(UUID cardId, UUID sellerId, int price) {
        this.cardId = cardId;
        this.sellerId = sellerId;
        this.price = price;
    }

    public MarketDTO(UUID cardId) {
        this(cardId, null, 0);
    }

    public UUID cardId() {
        return cardId;
    }

    public UUID sellerId() {
        return sellerId;
    }

    public int price() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketDTO marketDTO = (MarketDTO) o;
        return price == marketDTO.price && 
               Objects.equals(cardId, marketDTO.cardId) && 
               Objects.equals(sellerId, marketDTO.sellerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, sellerId, price);
    }

    @Override
    public String toString() {
        return "MarketDTO{" +
                "cardId=" + cardId +
                ", sellerId=" + sellerId +
                ", price=" + price +
                '}';
    }
}