package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

// Point d'entrée CLI étendu pour le TP3
public final class Main {

    private static final String CSV_FILE = "reservations.csv";

    public static void main(String[] args) {
        ReservationService svc = new ReservationService();
        ReservationCsvStore store = new ReservationCsvStore();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                if (!sc.hasNextLine()) break;

                String[] parts = sc.nextLine().trim().split("\\s+");
                if (parts.length == 0 || parts[0].isEmpty()) continue;

                switch (parts[0].toLowerCase()) {
                    case "quit" -> { return; }
                    case "resources" -> svc.listResources().forEach(r -> System.out.println("OK " + r.id() + " " + r.label() + " " + r.type()));
                    case "reservations" -> svc.listReservations().forEach(r -> System.out.println("OK id=" + r.id() + " user=" + r.user() + " status=" + r.status()));
                    
                    case "reserve" -> {
                        if (parts.length != 5) {
                            System.out.println("ERR INVALID_TIME usage: reserve <user> <resourceId> <start> <end>");
                        } else {
                            try {
                                Result<Reservation> res = svc.createReservation(parts[1], Long.parseLong(parts[2]), LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
                                System.out.println(res.isOk() ? "OK " + res.message() : "ERR " + res.error() + " " + res.message());
                            } catch (Exception e) {
                                System.out.println("ERR INVALID_TIME bad_input");
                            }
                        }
                    }
                    
                    case "cancel" -> {
                        if (parts.length != 2) {
                            System.out.println("ERR INVALID_TIME usage: cancel <reservationId>");
                        } else {
                            try {
                                Result<Reservation> res = svc.cancel(Long.parseLong(parts[1]));
                                System.out.println(res.isOk() ? "OK cancelled " + res.message() : "ERR " + res.error() + " " + res.message());
                            } catch (Exception e) {
                                System.out.println("ERR INVALID_TIME bad_input");
                            }
                        }
                    }

                    // --- NOUVELLES COMMANDES TP3 ---
                    case "save" -> {
                        store.save(CSV_FILE, svc.listReservations());
                        System.out.println("OK saved");
                    }
                    
                    case "load" -> {
                        List<Reservation> loaded = store.load(CSV_FILE);
                        svc.replaceReservations(loaded);
                        System.out.println("OK loaded count=" + loaded.size());
                    }
                    
                    case "findbyuser" -> {
                        if (parts.length != 2) {
                            System.out.println("ERR NOT_FOUND usage: findByUser <user>");
                        } else {
                            List<Reservation> found = svc.findByUser(parts[1]);
                            System.out.println("OK count=" + found.size());
                        }
                    }
                    
                    case "findbyresource" -> {
                        if (parts.length != 2) {
                            System.out.println("ERR NOT_FOUND usage: findByResource <resourceId>");
                        } else {
                            try {
                                List<Reservation> found = svc.findByResource(Long.parseLong(parts[1]));
                                System.out.println("OK count=" + found.size());
                            } catch (Exception e) {
                                System.out.println("ERR NOT_FOUND bad_input");
                            }
                        }
                    }

                    default -> System.out.println("ERR NOT_FOUND unknown_command");
                }
            }
        }
    }
}