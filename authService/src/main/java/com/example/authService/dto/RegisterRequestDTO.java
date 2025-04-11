package com.example.authService.dto;

public record RegisterRequestDTO(
        String username,
        String email,
        String password
) {
}
