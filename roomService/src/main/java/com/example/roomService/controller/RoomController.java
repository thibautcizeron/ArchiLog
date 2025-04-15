package com.example.roomService.controller;

import com.example.roomService.dto.RoomDTO;
import com.example.roomService.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.roomService.model.RoomStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrôleur REST pour gérer les rooms.
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * Liste toutes les rooms (debug ou admin).
     */
    @GetMapping
    public List<RoomDTO> getAllRooms() {
        return roomService.getAllRooms();
    }

    /**
     * Récupérer toutes les rooms par statut (GET /rooms/status?value=FULL)
     * @param value
     * @return
     */
    @GetMapping("/status")
    public List<RoomDTO> getRoomsByStatus(@RequestParam RoomStatus value) {
        return roomService.getRoomsByStatus(value);
    }

    /**
     * Liste les rooms avec statut WAITING (disponibles à rejoindre).
     */
    @GetMapping("/waiting")
    public List<RoomDTO> getWaitingRooms() {
        return roomService.getWaitingRooms();
    }

    /**
     * Création d'une nouvelle room par un utilisateur.
     */
    @PostMapping("/create")
    public ResponseEntity<RoomDTO> createRoom(@RequestParam UUID playerId) {
        RoomDTO room = roomService.createRoom(playerId);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        return roomService.deleteRoom(roomId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }


    /**
     * Rejoindre une room en attente (si disponible).
     */
    @PostMapping("/join")
    public ResponseEntity<RoomDTO> joinRoom(@RequestParam UUID playerId) {
        Optional<RoomDTO> joined = roomService.joinRoom(playerId);
        return joined
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomDTO> joinRoomById(@PathVariable UUID roomId, @RequestParam UUID playerId) {
        Optional<RoomDTO> joined = roomService.joinRoomById(roomId, playerId);
        return joined
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(409).build()); // Conflit si déjà pleine
    }


    /**
     * Lancer une partie depuis une room (si deux joueurs présents).
     */
    @PostMapping("/{roomId}/start")
    public ResponseEntity<Void> launchGame(@PathVariable UUID roomId) {
        boolean success = roomService.launchGame(roomId);
        return success
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    /**
     *
     * Récupérer une room par ID (GET /rooms/{roomId})
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable UUID roomId) {
        return roomService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
