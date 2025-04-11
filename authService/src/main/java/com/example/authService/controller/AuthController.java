package com.example.authService.controller;

import com.example.authService.dto.LoginRequestDTO;
import com.example.authService.dto.RegisterRequestDTO;
import com.example.authService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequest) {
        return authService.login(loginRequest)
                .<ResponseEntity<Object>>map(authResponse -> ResponseEntity.ok().body(authResponse))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Identifiants invalides")));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO registerRequest) {
        return authService.register(registerRequest)
                .<ResponseEntity<Object>>map(authResponse -> ResponseEntity.ok().body(authResponse))
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de l'enregistrement")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        boolean success = authService.logout(token);

        if (success) {
            return ResponseEntity.ok().body(Map.of("message", "Déconnexion réussie"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la déconnexion"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        boolean valid = authService.validateToken(token);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return ResponseEntity.ok(response);
    }
}