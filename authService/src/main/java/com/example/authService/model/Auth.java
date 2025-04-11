package com.example.authService.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "auth_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID userId;
    private String token;
    private boolean active;
    private long expiresAt;
}