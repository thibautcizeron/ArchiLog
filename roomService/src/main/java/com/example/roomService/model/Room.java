package com.example.roomService.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID player1Id;

    private UUID player2Id;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;
}
