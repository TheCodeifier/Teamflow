package database.model;

import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task in the system with berichtID, trelloID, and description.
 * Provides methods for database operations related to task management.
 */
class Taak {
    private int berichtID;
    private int trelloID;
    private String beschrijving;

    /**
     * Checks if a task with the given message ID exists in the database.
     *
     * @param berichtID The message ID to check
     * @return true if the task exists, false otherwise
     */
    public static boolean exists(int berichtID) {
        if (berichtID <= 0) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT COUNT(*) FROM TAAK WHERE berichtID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, berichtID);

            // Execute query
            rs = stmt.executeQuery();

            // Return true if count > 0, false otherwise
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking if task exists: " + e.getMessage());
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
     * Retrieves a task from the database by message ID.
     * Uses the exists method to first check if the task exists.
     *
     * @param berichtID The message ID to look up
     * @return Taak object if found, null otherwise
     */
    public static Taak lookup(int berichtID) {
        // Validate input
        if (berichtID <= 0) {
            return null;
        }

        // First check if the task exists using the exists method
        if (!exists(berichtID)) {
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT berichtID, trelloID, beschrijving FROM TAAK WHERE berichtID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, berichtID);

            // Execute query
            rs = stmt.executeQuery();

            // Check if task exists and return it
            if (rs.next()) {
                int msgId = rs.getInt("berichtID");
                int trelId = rs.getInt("trelloID");
                String description = rs.getString("beschrijving");
                return new Taak(msgId, trelId, description);
            }

            // Return null if task not found (shouldn't happen since we checked with exists)
            return null;

        } catch (SQLException e) {
            System.out.println("Error retrieving task: " + e.getMessage());
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
     * Retrieves all tasks from the database.
     *
     * @return List of all Taak objects in the database, empty list if none found or if an error occurs
     */
    public static List<Taak> getAll() {
        List<Taak> taken = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select all tasks
            String sql = "SELECT berichtID, trelloID, beschrijving FROM TAAK";
            stmt = conn.prepareStatement(sql);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of tasks
            while (rs.next()) {
                int msgId = rs.getInt("berichtID");
                int trelId = rs.getInt("trelloID");
                String description = rs.getString("beschrijving");

                Taak taak = new Taak(msgId, trelId, description);
                taken.add(taak);
            }

            return taken;

        } catch (SQLException e) {
            System.out.println("Error retrieving all tasks: " + e.getMessage());
            e.printStackTrace();
            return taken; // Return empty list in case of error
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
     * Retrieves all tasks for a specific Trello board.
     *
     * @param trelloID The ID of the Trello board
     * @return List of all Taak objects linked to the specified Trello board, empty list if none found or if an error occurs
     */
    public static List<Taak> getByTrelloID(int trelloID) {
        List<Taak> taken = new ArrayList<>();

        // Validate input
        if (trelloID <= 0) {
            return taken;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select tasks by trelloID
            String sql = "SELECT berichtID, trelloID, beschrijving FROM TAAK WHERE trelloID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trelloID);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of tasks
            while (rs.next()) {
                int msgId = rs.getInt("berichtID");
                int trelId = rs.getInt("trelloID");
                String description = rs.getString("beschrijving");

                Taak taak = new Taak(msgId, trelId, description);
                taken.add(taak);
            }

            return taken;

        } catch (SQLException e) {
            System.out.println("Error retrieving tasks by Trello ID: " + e.getMessage());
            e.printStackTrace();
            return taken; // Return empty list in case of error
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
     * Saves the current task to the database.
     * If the task already exists, updates the trelloID and description.
     * If the task doesn't exist, creates a new record.
     * Uses the exists method to check existence.
     *
     * @throws IllegalArgumentException if berichtID or trelloID is invalid
     * @throws SQLException if a database error occurs
     */
    public void save() throws IllegalArgumentException, SQLException {
        // Validate berichtID
        if (this.getBerichtID() <= 0) {
            throw new IllegalArgumentException("BerichtID must be greater than 0");
        }

        // Validate trelloID
        if (this.getTrelloID() <= 0) {
            throw new IllegalArgumentException("TrelloID must be greater than 0");
        }

        // Validate beschrijving
        if (this.getBeschrijving() == null) {
            throw new IllegalArgumentException("Beschrijving cannot be null");
        }

        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Check if task already exists using the exists method
            boolean taskExists = exists(this.getBerichtID());

            // Update or insert based on existence
            if (taskExists) {
                // Update existing task
                String updateSql = "UPDATE TAAK SET trelloID = ?, beschrijving = ? WHERE berichtID = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, this.getTrelloID());
                updateStmt.setString(2, this.getBeschrijving());
                updateStmt.setInt(3, this.getBerichtID());
                updateStmt.executeUpdate();
            } else {
                // Insert new task
                String insertSql = "INSERT INTO TAAK (berichtID, trelloID, beschrijving) VALUES (?, ?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, this.getBerichtID());
                insertStmt.setInt(2, this.getTrelloID());
                insertStmt.setString(3, this.getBeschrijving());
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
     * Creates a new Taak with the specified message ID, Trello ID and description.
     *
     * @param berichtID the message ID for the task
     * @param trelloID the Trello ID for the task
     * @param beschrijving the description for the task
     */
    public Taak(int berichtID, int trelloID, String beschrijving) {
        this.berichtID = berichtID;
        this.trelloID = trelloID;
        this.beschrijving = beschrijving;
    }

    /**
     * Deletes this task from the database.
     *
     * @return true if the task was successfully deleted, false if the task didn't exist or an error occurred
     */
    public boolean delete() {
        if (this.getBerichtID() <= 0) {
            return false;
        }

        // Only attempt to delete if the task exists
        if (!exists(this.getBerichtID())) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Prepare delete statement
            String sql = "DELETE FROM TAAK WHERE berichtID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.getBerichtID());

            // Execute delete operation
            int rowsAffected = stmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
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

    public int getBerichtID() {
        return berichtID;
    }

    public void setBerichtID(int berichtID) {
        this.berichtID = berichtID;
    }

    public int getTrelloID() {
        return trelloID;
    }

    public void setTrelloID(int trelloID) {
        this.trelloID = trelloID;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }
}