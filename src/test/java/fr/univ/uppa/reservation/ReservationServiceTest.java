package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

// Vérification des scénarios imposés par le TP2
public class ReservationServiceTest {

    @Test
    void create_ok() {
        ReservationService svc = new ReservationService();
        Result<Reservation> res = svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 2, 10, 10, 0), LocalDateTime.of(2026, 2, 10, 11, 0));
        assertTrue(res.isOk());
        assertEquals(Status.CONFIRMED, res.value().status());
    }

    @Test
    void create_overlap_conflict() {
        ReservationService svc = new ReservationService();
        svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 2, 10, 10, 0), LocalDateTime.of(2026, 2, 10, 11, 0));
        Result<Reservation> res = svc.createReservation("Bob", 1L, LocalDateTime.of(2026, 2, 10, 10, 30), LocalDateTime.of(2026, 2, 10, 11, 30));
        assertFalse(res.isOk());
        assertEquals(ErrorCode.CONFLICT, res.error());
    }

    @Test
    void create_boundary_not_conflict() {
        ReservationService svc = new ReservationService();
        svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 2, 10, 10, 0), LocalDateTime.of(2026, 2, 10, 11, 0));
        // Teste la frontière exacte entre les deux créneaux
        Result<Reservation> res = svc.createReservation("Bob", 1L, LocalDateTime.of(2026, 2, 10, 11, 0), LocalDateTime.of(2026, 2, 10, 12, 0));
        assertTrue(res.isOk());
    }

    @Test
    void cancel_releases_slot() {
        ReservationService svc = new ReservationService();
        Result<Reservation> created = svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 2, 10, 10, 0), LocalDateTime.of(2026, 2, 10, 11, 0));
        svc.cancel(created.value().id()); 
        
        Result<Reservation> newRes = svc.createReservation("Bob", 1L, LocalDateTime.of(2026, 2, 10, 10, 30), LocalDateTime.of(2026, 2, 10, 11, 30));
        assertTrue(newRes.isOk());
    }
}