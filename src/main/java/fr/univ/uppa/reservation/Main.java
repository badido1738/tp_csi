package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

// Point d'entrée CLI (sans save/load car la DB est persistante en direct)
public final class Main {
    public static void main(String[] args) {
        ReservationService svc = new ReservationService("reservations.db");

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                if (!sc.hasNextLine()) break;
                String[] parts = sc.nextLine().trim().split("\\s+");
                if (parts.length == 0 || parts[0].isEmpty()) continue;

                switch (parts[0].toLowerCase()) {
                    case "quit" -> { return; }
                    case "resources" -> svc.listResources().forEach(r -> System.out.println("OK " + r.id() + " " + r.label() + " " + r.type()));
                    case "reservations" -> {
                        List<Reservation> res = svc.listReservations();
                        System.out.println("OK count=" + res.size());
                        res.forEach(r -> System.out.println(r.id() + " " + r.user() + " " + r.start() + " " + r.end() + " " + r.status()));
                    }
                    case "reserve" -> {
                        if (parts.length != 5) System.out.println("ERR INVALID_TIME usage: reserve <user> <resourceId> <start> <end>");
                        else try {
                            Result<Reservation> res = svc.createReservation(parts[1], Long.parseLong(parts[2]), LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
                            System.out.println(res.isOk() ? "OK " + res.message() : "ERR " + res.error() + " " + res.message());
                        } catch (Exception e) { System.out.println("ERR INVALID_TIME bad_input"); }
                    }
                    case "cancel" -> {
                        if (parts.length != 2) System.out.println("ERR INVALID_TIME usage: cancel <id>");
                        else try {
                            Result<Reservation> res = svc.cancel(Long.parseLong(parts[1]));
                            System.out.println(res.isOk() ? "OK " + res.message() : "ERR " + res.error() + " " + res.message());
                        } catch (Exception e) { System.out.println("ERR INVALID_TIME bad_input"); }
                    }
                    case "findbyuser" -> {
                        if (parts.length != 2) System.out.println("ERR NOT_FOUND usage: findByUser <user>");
                        else System.out.println("OK count=" + svc.findByUser(parts[1]).size());
                    }
                    case "findbyresource" -> {
                        if (parts.length != 2) System.out.println("ERR NOT_FOUND usage: findByResource <id>");
                        else try {
                            System.out.println("OK count=" + svc.findByResource(Long.parseLong(parts[1])).size());
                        } catch (Exception e) { System.out.println("ERR NOT_FOUND bad_input"); }
                    }
                    default -> System.out.println("ERR NOT_FOUND unknown_command");
                }
            }
        }
    }
}