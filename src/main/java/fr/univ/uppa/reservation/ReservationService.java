package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Gère la logique métier des réservations et valide les règles
public final class ReservationService {

    private final List<Resource> resources = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private long nextId = 1;

    public ReservationService() {
        resources.add(new Resource(1L, "Salle A", "ROOM"));
        resources.add(new Resource(2L, "Projecteur", "EQUIPMENT"));
    }

    public List<Resource> listResources() {
        return List.copyOf(resources);
    }

    public List<Reservation> listReservations() {
        return List.copyOf(reservations);
    }

    public Result<Reservation> createReservation(String user, long resourceId, LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) return Result.fail(ErrorCode.INVALID_TIME, "start>=end");
        if (!resourceExists(resourceId)) return Result.fail(ErrorCode.RESOURCE_NOT_FOUND, "id=" + resourceId);

        for (Reservation r : reservations) {
            // On ignore les réservations annulées lors de la détection de conflit
            if (r.resourceId() == resourceId && r.status() != Status.CANCELLED) {
                if (overlap(start, end, r.start(), r.end())) {
                    return Result.fail(ErrorCode.CONFLICT, "overlap");
                }
            }
        }

        Reservation newReservation = new Reservation(nextId++, user, resourceId, start, end, Status.CONFIRMED);
        reservations.add(newReservation);
        return Result.ok(newReservation, "reservationId=" + newReservation.id());
    }

    public Result<Reservation> cancel(long reservationId) {
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            if (r.id() == reservationId) {
                Reservation cancelledRes = new Reservation(r.id(), r.user(), r.resourceId(), r.start(), r.end(), Status.CANCELLED);
                reservations.set(i, cancelledRes);
                return Result.ok(cancelledRes, "reservationId=" + reservationId);
            }
        }
        return Result.fail(ErrorCode.NOT_FOUND, "reservationId=" + reservationId);
    }

    static boolean overlap(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    private boolean resourceExists(long resourceId) {
        return resources.stream().anyMatch(r -> r.id() == resourceId);
    }
}