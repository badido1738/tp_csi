package fr.univ.uppa.reservation;

import java.time.LocalDateTime;

public record Reservation(long id, long resourceId, LocalDateTime start, LocalDateTime end) {
}