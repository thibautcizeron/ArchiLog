package com.example.roomService.service;

import com.example.roomService.dto.RoomDTO;
import com.example.roomService.model.Room;
import com.example.roomService.model.RoomStatus;
import com.example.roomService.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * Récupère toutes les rooms existantes (utile pour debug ou affichage complet).
     */
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Récupère uniquement les rooms en attente de joueurs.
     */
    public List<RoomDTO> getWaitingRooms() {
        return roomRepository.findByStatus(RoomStatus.WAITING).stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Permet à un joueur de créer une nouvelle room.
     */
    public RoomDTO createRoom(UUID playerId) {
        Room newRoom = Room.builder()
                .player1Id(playerId)
                .player2Id(null)
                .status(RoomStatus.WAITING)
                .build();

        return toDTO(roomRepository.save(newRoom));
    }

    public boolean deleteRoom(UUID roomId) {
        if (roomRepository.existsById(roomId)) {
            roomRepository.deleteById(roomId);
            return true;
        }
        return false;
    }

    /**
     *
     * Récupérer une room par ID (GET /rooms/{roomId})
     *
     */
    public Optional<RoomDTO> getRoomById(UUID roomId) {
        return roomRepository.findById(roomId).map(this::toDTO);
    }

    /**
     * Récupérer toutes les rooms par statut (GET /rooms/status?value=FULL)
     * @param status
     * @return
     */
    public List<RoomDTO> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .toList();
    }




    /**
     * Permet à un joueur de rejoindre une room existante en attente.
     */
    public Optional<RoomDTO> joinRoom(UUID playerId) {
        Optional<Room> roomOpt = roomRepository.findFirstByStatusAndPlayer2IdIsNull(RoomStatus.WAITING);

        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setPlayer2Id(playerId);
            room.setStatus(RoomStatus.FULL);
            return Optional.of(toDTO(roomRepository.save(room)));
        }

        return Optional.empty(); // Aucune room dispo
    }
    /**
     * Permet à un joueur de rejoindre une room existante en spécifiant l'id de la room.
     */
    public Optional<RoomDTO> joinRoomById(UUID roomId, UUID playerId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);

        if (roomOpt.isEmpty()) return Optional.empty();

        Room room = roomOpt.get();
        if (room.getPlayer2Id() != null || room.getStatus() != RoomStatus.WAITING) {
            return Optional.empty();
        }

        room.setPlayer2Id(playerId);
        room.setStatus(RoomStatus.FULL);
        return Optional.of(toDTO(roomRepository.save(room)));
    }


    /**
     * Lance une partie depuis une room si elle est complète.
     * TODO : Ajouter un appel au gameService.
     */
    public boolean launchGame(UUID roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);

        if (roomOpt.isEmpty()) return false;

        Room room = roomOpt.get();

        if (room.getStatus() != RoomStatus.FULL) return false;

        // Mise à jour du statut (et plus tard : appel à gameService)
        room.setStatus(RoomStatus.STARTED);
        roomRepository.save(room);

        // Ici, on pourrait notifier gameService avec les 2 joueurs via RestTemplate

        return true;
    }

    private RoomDTO toDTO(Room room) {
        return new RoomDTO(room.getId(), room.getPlayer1Id(), room.getPlayer2Id(), room.getStatus());
    }
}
