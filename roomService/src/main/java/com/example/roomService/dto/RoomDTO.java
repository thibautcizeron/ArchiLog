package com.example.roomService.dto;

import com.example.roomService.model.RoomStatus;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) représentant une Room.
 * Utilisé pour envoyer ou recevoir les données de Room depuis l'API.
 */
public record RoomDTO(
        UUID id,
        UUID player1Id,
        UUID player2Id,
        RoomStatus status
) {}
