package com.example.authService.service;

import com.example.authService.dto.AuthResponseDTO;
import com.example.authService.dto.LoginRequestDTO;
import com.example.authService.dto.RegisterRequestDTO;
import com.example.authService.dto.UserDTO;
import com.example.authService.model.Auth;
import com.example.authService.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    private final String userServiceUrl = "http://user-service:8080/api/users";

    public Optional<AuthResponseDTO> login(LoginRequestDTO loginRequest) {
        try {
            ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(
                    userServiceUrl + "/search?keyword=" + loginRequest.username(),
                    UserDTO[].class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Optional.empty();
            }

            for (UserDTO user : response.getBody()) {
                if (user.username().equals(loginRequest.username()) &&
                        passwordEncoder.matches(loginRequest.password(), user.password())) {

                    String token = jwtService.generateToken(user.id());

                    authRepository.findByUserIdAndActive(user.id(), true)
                            .ifPresent(authRepository::delete);

                    Auth session = Auth.builder()
                            .userId(user.id())
                            .token(token)
                            .active(true)
                            .expiresAt(System.currentTimeMillis() + 86400000)

                            .build();

                    authRepository.save(session);

                    return Optional.of(new AuthResponseDTO(token, user.id(), user.username()));
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Erreur lors de la communication avec userService: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<AuthResponseDTO> register(RegisterRequestDTO registerRequest) {
        try {
            String hashedPassword = passwordEncoder.encode(registerRequest.password());

            UserDTO newUser = new UserDTO(
                    null,
                    registerRequest.username(),
                    registerRequest.email(),
                    hashedPassword
            );

            HttpEntity<UserDTO> request = new HttpEntity<>(newUser);
            ResponseEntity<UserDTO> response = restTemplate.postForEntity(
                    userServiceUrl,
                    request,
                    UserDTO.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Optional.empty();
            }

            UserDTO createdUser = response.getBody();
            String token = jwtService.generateToken(createdUser.id());

            Auth session = Auth.builder()
                    .userId(createdUser.id())
                    .token(token)
                    .active(true)
                    .expiresAt(System.currentTimeMillis() + 86400000)
                    .build();

            authRepository.save(session);

            return Optional.of(new AuthResponseDTO(token, createdUser.id(), createdUser.username()));
        } catch (Exception e) {
            System.err.println("Erreur lors de la communication avec userService: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean logout(String token) {
        try {
            Optional<Auth> sessionOpt = authRepository.findByToken(token);
            if (sessionOpt.isPresent()) {
                Auth session = sessionOpt.get();
                session.setActive(false);
                authRepository.save(session);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        if (!jwtService.validateToken(token)) {
            return false;
        }

        return authRepository.findByToken(token)
                .map(Auth::isActive)
                .orElse(false);
    }
}