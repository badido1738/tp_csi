package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.List;

// Le service gère les règles métier et délègue le stockage au Store
public final class ReservationService {
    private final ReservationSqliteStore store;
    private final List<Resource> resources = List.of(
            new Resource(1L, "Salle A", "ROOM"),
            new Resource(2L, "Projecteur", "EQUIPMENT")
    );

    public ReservationService(String dbName) {
        this.store = new ReservationSqliteStore(dbName);
    }

    public List<Resource> listResources() { return resources; }
    public List<Reservation> listReservations() { return store.findAll(); }
    public List<Reservation> findByUser(String user) { return store.findByUser(user); }
    public List<Reservation> findByResource(long resId) { return store.findByResource(resId); }

    public Result<Reservation> createReservation(String user, long resId, LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) return Result.fail(ErrorCode.INVALID_TIME, "start>=end");
        if (resources.stream().noneMatch(r -> r.id() == resId)) return Result.fail(ErrorCode.RESOURCE_NOT_FOUND, "id=" + resId);

        // Récupère uniquement les réservations de la ressource demandée depuis la DB
        for (Reservation r : store.findByResource(resId)) {
            if (r.status() != Status.CANCELLED && overlap(start, end, r.start(), r.end())) {
                return Result.fail(ErrorCode.CONFLICT, "overlap");
            }
        }

        Reservation saved = store.saveReservation(new Reservation(0, user, resId, start, end, Status.CONFIRMED));
        return Result.ok(saved, "reservationId=" + saved.id());
    }

    public Result<Reservation> cancel(long id) {
        boolean updated = store.updateStatus(id, Status.CANCELLED);
        if (updated) return Result.ok(null, "cancelled reservationId=" + id);
        return Result.fail(ErrorCode.NOT_FOUND, "reservationId=" + id);
    }

    static boolean overlap(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}