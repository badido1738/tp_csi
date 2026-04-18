package fr.univ.uppa.reservation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Gère la sauvegarde et le chargement des réservations au format CSV
public final class ReservationCsvStore {

    private static final String HEADER = "id;user;resourceId;start;end;status";

    public List<Reservation> load(String path) {
        List<Reservation> list = new ArrayList<>();
        try {
            Path filePath = Path.of(path);
            if (!Files.exists(filePath)) return list;

            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) return list;

            // On ignore la première ligne (l'en-tête)
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                
                String[] parts = line.split(";");
                list.add(new Reservation(
                        Long.parseLong(parts[0]),
                        parts[1],
                        Long.parseLong(parts[2]),
                        LocalDateTime.parse(parts[3]),
                        LocalDateTime.parse(parts[4]),
                        Status.valueOf(parts[5])
                ));
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("ERR IO_ERROR " + e.getMessage());
        }
        return list;
    }

    public void save(String path, List<Reservation> reservations) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Reservation r : reservations) {
            lines.add(r.id() + ";" + r.user() + ";" + r.resourceId() + ";"
                    + r.start() + ";" + r.end() + ";" + r.status());
        }
        try {
            Files.write(Path.of(path), lines);
        } catch (IOException e) {
            System.out.println("ERR IO_ERROR " + e.getMessage());
        }
    }
}