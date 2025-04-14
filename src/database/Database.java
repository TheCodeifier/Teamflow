package database;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:database/db.sqlite";

    // Single instance of the class
    private static Database instance;

    // Database connection
    private Connection connection;

    private final boolean log = false;

    // Private constructor to prevent instantiation
    private Database() {
        // Initialize connection in constructor
        try {
            if (log) System.out.println("Connecting to: " + DB_URL);
            this.connection = DriverManager.getConnection(DB_URL);
            if (log) System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Static method to get the singleton instance
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Get the database connection
    public Connection getConnection() {
        return connection;
    }

    // Close the connection when needed
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}