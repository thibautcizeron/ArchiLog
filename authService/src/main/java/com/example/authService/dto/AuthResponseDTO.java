package com.example.authService.dto;

import java.util.UUID;

public record AuthResponseDTO(
        String token,
        UUID userId,
        String username
) {
}
