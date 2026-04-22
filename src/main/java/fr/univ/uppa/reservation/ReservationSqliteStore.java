package fr.univ.uppa.reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Gère exclusivement les requêtes SQL (CRUD) vers SQLite
public final class ReservationSqliteStore {
    private final String url;

    public ReservationSqliteStore(String dbName) {
        this.url = "jdbc:sqlite:" + dbName;
        // Création de la table si elle n'existe pas
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS reservation (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_name TEXT NOT NULL, " +
                    "resource_id INTEGER NOT NULL, " +
                    "start_time TEXT NOT NULL, " +
                    "end_time TEXT NOT NULL, " +
                    "status TEXT NOT NULL)");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Reservation> findAll() {
        return query("SELECT * FROM reservation");
    }

    public List<Reservation> findByUser(String user) {
        return queryWithParam("SELECT * FROM reservation WHERE user_name = ?", user);
    }

    public List<Reservation> findByResource(long resourceId) {
        return queryWithParam("SELECT * FROM reservation WHERE resource_id = ?", String.valueOf(resourceId));
    }

    public Reservation saveReservation(Reservation r) {
        String sql = "INSERT INTO reservation(user_name, resource_id, start_time, end_time, status) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, r.user());
            pstmt.setLong(2, r.resourceId());
            pstmt.setString(3, r.start().toString());
            pstmt.setString(4, r.end().toString());
            pstmt.setString(5, r.status().name());
            pstmt.executeUpdate();
            
            // Récupère l'ID généré automatiquement par SQLite
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Reservation(keys.getLong(1), r.user(), r.resourceId(), r.start(), r.end(), r.status());
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return r;
    }

    public boolean updateStatus(long reservationId, Status status) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE reservation SET status = ? WHERE id = ?")) {
            pstmt.setString(1, status.name());
            pstmt.setLong(2, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    // Méthodes utilitaires pour éviter la duplication de code JDBC
    private List<Reservation> query(String sql) {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private List<Reservation> queryWithParam(String sql, String param) {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getLong("id"), rs.getString("user_name"), rs.getLong("resource_id"),
                LocalDateTime.parse(rs.getString("start_time")), LocalDateTime.parse(rs.getString("end_time")),
                Status.valueOf(rs.getString("status"))
        );
    }
}