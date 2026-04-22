package fr.univ.uppa.reservation;

import java.time.LocalDateTime;

public class Demo {
    public static void main(String[] args) {
        ReservationService svc = new ReservationService(null);
        var r1 = svc.createReservation(null, 1L, LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 11, 0));
        System.out.println(r1);

        var r2 = svc.createReservation(null, 1L, LocalDateTime.of(2026, 2, 10, 10, 30),
                LocalDateTime.of(2026, 2, 10, 11, 30));
        System.out.println(r2); // should be CONFLICT
    }
}
