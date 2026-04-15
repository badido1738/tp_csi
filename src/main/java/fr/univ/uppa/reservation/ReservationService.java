package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ReservationService {

    private static boolean resourceExists(long resourceId) {
        return resourceId == 1L || resourceId == 2L;
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private long nextId = 1;

    public List<Reservation> allReservations() {
        return List.copyOf(reservations);
    }

    public Result<Reservation> createReservation(long resourceId, LocalDateTime start, LocalDateTime end) {
        // Rule 1: validate time: start < end
        if (!start.isBefore(end)) {
            return Result.fail(ErrorCode.INVALID_TIME);
        }

        // Rule 2: validate resource exists (1 or 2)
        if (!resourceExists(resourceId)) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // Rule 3: check overlap on same resource
        for (Reservation existingRes : reservations) {
            if (existingRes.resourceId() == resourceId) {
                if (overlap(existingRes.start(), existingRes.end(), start, end)) {
                    return Result.fail(ErrorCode.CONFLICT);
                }
            }
        }

        // If ok -> store new reservation and return ok
        Reservation newReservation = new Reservation(nextId++, resourceId, start, end);
        reservations.add(newReservation);
        return Result.ok(newReservation);
    }

    static boolean overlap(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        // Implement using: s1 < e2 AND s2 < e1
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}