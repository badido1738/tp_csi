package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class ReservationServiceTest {

    @Test
    void test_create_ok() {
        // Given
        ReservationService service = new ReservationService();
        LocalDateTime start = LocalDateTime.of(2026, 2, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 10, 11, 0);

        // When
        Result<Reservation> result = service.createReservation(1L, start, end);

        // Then
        assertTrue(result.isOk(), "La réservation devrait être un succès");
        assertNotNull(result.value(), "La valeur de retour ne doit pas être nulle");
        assertEquals(1L, result.value().id(), "Le premier ID doit être 1");
        assertEquals(ErrorCode.NONE, result.error());
    }

    @Test
    void test_create_overlap_conflict() {
        // Given
        ReservationService service = new ReservationService();
        service.createReservation(1L, 
            LocalDateTime.of(2026, 2, 10, 10, 0), 
            LocalDateTime.of(2026, 2, 10, 11, 0)); // Déjà réservé de 10h à 11h

        LocalDateTime startConflict = LocalDateTime.of(2026, 2, 10, 10, 30);
        LocalDateTime endConflict = LocalDateTime.of(2026, 2, 10, 11, 30);

        // When
        Result<Reservation> result = service.createReservation(1L, startConflict, endConflict);

        // Then
        assertFalse(result.isOk(), "La réservation devrait échouer");
        assertNull(result.value(), "Aucune réservation ne doit être retournée");
        assertEquals(ErrorCode.CONFLICT, result.error(), "Le code d'erreur doit être CONFLICT");
    }

    @Test
    void test_create_boundary_not_conflict() {
        // Given
        ReservationService service = new ReservationService();
        service.createReservation(1L, 
            LocalDateTime.of(2026, 2, 10, 10, 0), 
            LocalDateTime.of(2026, 2, 10, 11, 0)); // Déjà réservé de 10h à 11h

        LocalDateTime startBoundary = LocalDateTime.of(2026, 2, 10, 11, 0);
        LocalDateTime endBoundary = LocalDateTime.of(2026, 2, 10, 12, 0);

        // When
        Result<Reservation> result = service.createReservation(1L, startBoundary, endBoundary);

        // Then
        assertTrue(result.isOk(), "La réservation limitrophe devrait être acceptée");
        assertNotNull(result.value(), "La réservation a dû être créée");
        assertEquals(2L, result.value().id(), "L'ID de la nouvelle réservation doit être 2");
        assertEquals(ErrorCode.NONE, result.error());
    }
}