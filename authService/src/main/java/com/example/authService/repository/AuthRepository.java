package com.example.authService.repository;

import com.example.authService.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<Auth, UUID> {
    Optional<Auth> findByToken(String token);
    Optional<Auth> findByUserIdAndActive(UUID userId, boolean active);
    void deleteByToken(String token);
}