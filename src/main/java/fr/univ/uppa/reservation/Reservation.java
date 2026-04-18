package fr.univ.uppa.reservation;

import java.time.LocalDateTime;

// Enregistrement immuable (record) représentant une réservation
public record Reservation(
        long id,
        String user,
        long resourceId,
        LocalDateTime start,
        LocalDateTime end,
        Status status
) {
}