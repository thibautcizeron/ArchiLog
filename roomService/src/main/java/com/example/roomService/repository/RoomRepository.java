package com.example.roomService.repository;

import com.example.roomService.model.Room;
import com.example.roomService.model.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour la gestion des Room en base de données.
 */
public interface RoomRepository extends JpaRepository<Room, UUID> {

    /**
     * Trouve toutes les rooms avec un statut donné.
     * @param status statut de la room (ex: WAITING)
     * @return liste des rooms correspondantes
     */
    List<Room> findByStatus(RoomStatus status);

    /**
     * Trouve une room où il manque un second joueur (room ouverte à rejoindre).
     * @param status statut de la room (ex: WAITING)
     * @param player2Id null attendu pour signifier slot libre
     * @return une room disponible
     */
    Optional<Room> findFirstByStatusAndPlayer2IdIsNull(RoomStatus status);
}
