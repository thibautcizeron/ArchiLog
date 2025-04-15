package com.example.authService.controller;

import com.example.authService.dto.LoginRequestDTO;
import com.example.authService.dto.RegisterRequestDTO;
import com.example.authService.service.AuthService;
import com.example.authService.dto.AuthResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest)
                .<ResponseEntity<Object>>map(authResponse -> {
                    ResponseCookie cookie = ResponseCookie.from("jwt", authResponse.token())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(3000)
                            .sameSite("Strict")
                            .build();

                    response.addHeader("Set-Cookie", cookie.toString());

                    return ResponseEntity.ok(Map.of(
                            "userId", authResponse.userId(),
                            "username", authResponse.username()
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Identifiants invalides")));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO registerRequest) {
        return authService.register(registerRequest)
                .<ResponseEntity<Object>>map(authResponse -> ResponseEntity.ok(Map.of(
                        "userId", authResponse.userId(),
                        "username", authResponse.username()
                )))
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de l'enregistrement")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "jwt", required = false) String token, HttpServletResponse response) {
        if (token == null || !authService.logout(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la déconnexion"));
        }

        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok().body(Map.of("message", "Déconnexion réussie"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@CookieValue(name = "jwt", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (token == null || !authService.validateToken(token)) {
            response.put("valid", false);
            return ResponseEntity.ok(response);
        }
        
        response.put("valid", true);
        authService.getUserInfoFromToken(token).ifPresent(userInfo -> {
            response.put("userId", userInfo.userId());
            response.put("username", userInfo.username());
        });
        
        return ResponseEntity.ok(response);
    }
}
