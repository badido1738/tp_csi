package fr.univ.uppa.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitDatabase {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reservations.db");
             Statement stmt = conn.createStatement()) {
             
            String sql =
                "CREATE TABLE IF NOT EXISTS reservation (" +
                "id INTEGER PRIMARY KEY, " +
                "user_name TEXT NOT NULL, " +
                "resource_id INTEGER NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "status TEXT NOT NULL" +
                ");";
                
            stmt.execute(sql);
            System.out.println("OK table created");
        }
    }
}