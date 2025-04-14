package com.example.userService.dto;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String username,
        String email,
        String password,
        String role
) {}
