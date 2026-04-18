package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests enrichis pour valider le Store CSV et les recherches
public class ReservationServiceTest {

    @Test
    void save_and_load_keeps_reservations() throws IOException {
        ReservationService svc = new ReservationService();
        ReservationCsvStore store = new ReservationCsvStore();
        Path tempFile = Files.createTempFile("test_reservations", ".csv");

        svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 3, 20, 10, 0), LocalDateTime.of(2026, 3, 20, 11, 0));
        svc.createReservation("Bob", 2L, LocalDateTime.of(2026, 3, 20, 14, 0), LocalDateTime.of(2026, 3, 20, 15, 0));

        // Save
        store.save(tempFile.toString(), svc.listReservations());

        // Load into a new service
        ReservationService newSvc = new ReservationService();
        newSvc.replaceReservations(store.load(tempFile.toString()));

        assertEquals(2, newSvc.listReservations().size(), "Les 2 réservations doivent être rechargées");
        assertEquals("Alice", newSvc.listReservations().get(0).user());
        
        Files.deleteIfExists(tempFile);
    }

    @Test
    void cancelled_reservation_stays_cancelled_after_load() throws IOException {
        ReservationService svc = new ReservationService();
        ReservationCsvStore store = new ReservationCsvStore();
        Path tempFile = Files.createTempFile("test_reservations_cancel", ".csv");

        Result<Reservation> res = svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 3, 20, 10, 0), LocalDateTime.of(2026, 3, 20, 11, 0));
        svc.cancel(res.value().id());

        store.save(tempFile.toString(), svc.listReservations());

        ReservationService newSvc = new ReservationService();
        newSvc.replaceReservations(store.load(tempFile.toString()));

        assertEquals(1, newSvc.listReservations().size());
        assertEquals(Status.CANCELLED, newSvc.listReservations().get(0).status(), "Le statut CANCELLED doit être conservé");

        Files.deleteIfExists(tempFile);
    }

    @Test
    void find_by_user_returns_correct_results() {
        ReservationService svc = new ReservationService();
        svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 3, 20, 10, 0), LocalDateTime.of(2026, 3, 20, 11, 0));
        svc.createReservation("Alice", 2L, LocalDateTime.of(2026, 3, 20, 14, 0), LocalDateTime.of(2026, 3, 20, 15, 0));
        svc.createReservation("Bob", 1L, LocalDateTime.of(2026, 3, 21, 10, 0), LocalDateTime.of(2026, 3, 21, 11, 0));

        List<Reservation> aliceReservations = svc.findByUser("Alice");
        
        assertEquals(2, aliceReservations.size(), "Alice devrait avoir 2 réservations");
        assertTrue(aliceReservations.stream().allMatch(r -> r.user().equals("Alice")));
    }
    
    @Test
    void next_id_is_recalculated_after_load() throws IOException {
        ReservationService svc = new ReservationService();
        ReservationCsvStore store = new ReservationCsvStore();
        Path tempFile = Files.createTempFile("test_next_id", ".csv");

        svc.createReservation("Alice", 1L, LocalDateTime.of(2026, 3, 20, 10, 0), LocalDateTime.of(2026, 3, 20, 11, 0)); // ID 1
        
        store.save(tempFile.toString(), svc.listReservations());

        ReservationService newSvc = new ReservationService();
        newSvc.replaceReservations(store.load(tempFile.toString())); // ID max est 1, donc le prochain devrait être 2
        
        Result<Reservation> newRes = newSvc.createReservation("Charlie", 2L, LocalDateTime.of(2026, 3, 20, 14, 0), LocalDateTime.of(2026, 3, 20, 15, 0));
        
        assertEquals(2L, newRes.value().id(), "Le nouvel ID doit être 2 (max + 1)");
        
        Files.deleteIfExists(tempFile);
    }
}