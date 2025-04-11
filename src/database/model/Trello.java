package database.model;

import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Trello board in the system.
 * Provides methods for database operations related to Trello boards.
 */
class Trello {
    private int trelloID;
    private String trelloURL;

    /**
     * Checks if a Trello board with the given ID exists in the database.
     *
     * @param trelloID The ID to check
     * @return true if the Trello board exists, false otherwise
     */
    public static boolean exists(int trelloID) {
        if (trelloID <= 0) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT COUNT(*) FROM TRELLO WHERE trelloID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trelloID);

            // Execute query
            rs = stmt.executeQuery();

            // Return true if count > 0, false otherwise
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking if Trello board exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Don't close connection here as it's managed by the Database singleton
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves a Trello board from the database by ID.
     * Uses the exists method to first check if the board exists.
     *
     * @param trelloID The ID to look up
     * @return Trello object if found, null otherwise
     */
    public static Trello lookup(int trelloID) {
        // Validate input
        if (trelloID <= 0) {
            return null;
        }

        // First check if the board exists using the exists method
        if (!exists(trelloID)) {
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT trelloID, trelloURL FROM TRELLO WHERE trelloID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trelloID);

            // Execute query
            rs = stmt.executeQuery();

            // Check if board exists and return it
            if (rs.next()) {
                int id = rs.getInt("trelloID");
                String url = rs.getString("trelloURL");
                return new Trello(id, url);
            }

            // Return null if board not found (shouldn't happen since we checked with exists)
            return null;

        } catch (SQLException e) {
            System.out.println("Error retrieving Trello board: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Don't close connection here as it's managed by the Database singleton
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves all Trello boards from the database.
     *
     * @return List of all Trello objects in the database, empty list if none found or if an error occurs
     */
    public static List<Trello> getAll() {
        List<Trello> trelloBoards = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select all Trello boards
            String sql = "SELECT trelloID, trelloURL FROM TRELLO";
            stmt = conn.prepareStatement(sql);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of Trello boards
            while (rs.next()) {
                int id = rs.getInt("trelloID");
                String url = rs.getString("trelloURL");

                Trello trello = new Trello(id, url);
                trelloBoards.add(trello);
            }

            return trelloBoards;

        } catch (SQLException e) {
            System.out.println("Error retrieving all Trello boards: " + e.getMessage());
            e.printStackTrace();
            return trelloBoards; // Return empty list in case of error
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Don't close connection here as it's managed by the Database singleton
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the current Trello board to the database.
     * If the board already exists, updates the URL.
     * If the board doesn't exist, creates a new record.
     * Uses the exists method to check existence.
     *
     * @throws IllegalArgumentException if trelloID is invalid
     * @throws SQLException if a database error occurs
     */
    public void save() throws IllegalArgumentException, SQLException {
        // Validate trelloID
        if (this.getTrelloID() <= 0) {
            throw new IllegalArgumentException("TrelloID must be greater than 0");
        }

        // Validate trelloURL
        if (this.getTrelloURL() == null || this.getTrelloURL().isEmpty()) {
            throw new IllegalArgumentException("TrelloURL cannot be empty or null");
        }

        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Check if board already exists using the exists method
            boolean boardExists = exists(this.getTrelloID());

            // Update or insert based on existence
            if (boardExists) {
                // Update existing board
                String updateSql = "UPDATE TRELLO SET trelloURL = ? WHERE trelloID = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, this.getTrelloURL());
                updateStmt.setInt(2, this.getTrelloID());
                updateStmt.executeUpdate();
            } else {
                // Insert new board
                String insertSql = "INSERT INTO TRELLO (trelloID, trelloURL) VALUES (?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, this.getTrelloID());
                insertStmt.setString(2, this.getTrelloURL());
                insertStmt.executeUpdate();
            }

        } finally {
            // Close resources
            try {
                if (insertStmt != null) insertStmt.close();
                if (updateStmt != null) updateStmt.close();
                // Don't close connection as it's managed by the Database singleton
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new Trello board with the specified ID and URL.
     *
     * @param trelloID the ID for the Trello board
     * @param trelloURL the URL for the Trello board
     */
    public Trello(int trelloID, String trelloURL) {
        this.trelloID = trelloID;
        this.trelloURL = trelloURL;
    }

    /**
     * Deletes this Trello board from the database.
     *
     * @return true if the board was successfully deleted, false if the board didn't exist or an error occurred
     */
    public boolean delete() {
        if (this.getTrelloID() <= 0) {
            return false;
        }

        // Only attempt to delete if the board exists
        if (!exists(this.getTrelloID())) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Prepare delete statement
            String sql = "DELETE FROM TRELLO WHERE trelloID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.getTrelloID());

            // Execute delete operation
            int rowsAffected = stmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting Trello board: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                // Don't close connection as it's managed by the Database singleton
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public int getTrelloID() {
        return trelloID;
    }

    public void setTrelloID(int trelloID) {
        this.trelloID = trelloID;
    }

    public String getTrelloURL() {
        return trelloURL;
    }

    public void setTrelloURL(String trelloURL) {
        this.trelloURL = trelloURL;
    }
}