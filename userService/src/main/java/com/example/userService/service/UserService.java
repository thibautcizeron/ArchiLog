package com.example.userService.service;

import com.example.userService.dto.UserDTO;
import com.example.userService.model.User;
import com.example.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getPassword(), 
            user.getRole(),
            user.getSolde()
        );
    }

    private User mapToEntity(UserDTO dto) {
        return User.builder()
                .id(dto.id())
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .role(dto.role() != null ? dto.role() : "USER")
                .solde(dto.solde() > 0 ? dto.solde() : 300)
                .build();
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::mapToDTO);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        user.setPassword(user.getPassword());
        return mapToDTO(userRepository.save(user));
    }

    public Optional<UserDTO> updateUser(UUID id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(userDTO.username());
                    existing.setEmail(userDTO.email());
                    existing.setPassword(userDTO.password());
                    existing.setRole(userDTO.role());
                    existing.setSolde(userDTO.solde());
                    return mapToDTO(userRepository.save(existing));
                });
    }

    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.searchByUsernameOrEmail(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> updateUserRole(UUID id, String role) {
        return userRepository.findById(id).map(user -> {
            user.setRole(role);
            return mapToDTO(userRepository.save(user));
        });
    }
    
    public Optional<UserDTO> updateUserSolde(UUID id, int solde) {
        return userRepository.findById(id).map(user -> {
            user.setSolde(solde);
            return mapToDTO(userRepository.save(user));
        });
    }
}