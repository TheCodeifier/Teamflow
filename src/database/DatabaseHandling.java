package database;

import java.sql.*;

public class DatabaseHandling {
    private static final String DB_URL = "jdbc:sqlite:database/db.sqlite";

    // Connect to the database
    public static Connection connect() {
        Connection conn = null;
        try {
            System.out.println("Connecting to: " + DB_URL);
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

}