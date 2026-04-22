package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {

    @Test
    void test_create_and_read_from_db() {
        // Utilisation d'une base en mémoire pour isoler le test
        ReservationService svc = new ReservationService(":memory:");
        Result<Reservation> res = svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 4, 10, 10, 0), LocalDateTime.of(2026, 4, 10, 11, 0));
        
        assertTrue(res.isOk());
        assertEquals(1, svc.listReservations().size());
        assertEquals("Alice", svc.listReservations().get(0).user());
    }

    @Test
    void test_cancel_updates_status() {
        ReservationService svc = new ReservationService(":memory:");
        Result<Reservation> res = svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 4, 10, 10, 0), LocalDateTime.of(2026, 4, 10, 11, 0));
        
        svc.cancel(res.value().id());
        
        assertEquals(Status.CANCELLED, svc.listReservations().get(0).status());
    }

    @Test
    void test_overlap_conflict_detected_with_db() {
        ReservationService svc = new ReservationService(":memory:");
        svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 4, 10, 10, 0), LocalDateTime.of(2026, 4, 10, 11, 0));
        
        Result<Reservation> res = svc.createReservation("Bob", 1L, LocalDateTime.of(2026, 4, 10, 10, 30), LocalDateTime.of(2026, 4, 10, 11, 30));
        
        assertFalse(res.isOk());
        assertEquals(ErrorCode.CONFLICT, res.error());
    }
}