package fr.univ.uppa.reservation;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestSqlite {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reservations.db");
        System.out.println("OK SQLite connected");
        conn.close();
    }
}