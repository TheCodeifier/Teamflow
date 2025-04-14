package database.model;

import database.Database;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sprint in the system with sprint number, begin date, and end date.
 * Provides methods for database operations related to sprint management.
 */
public class Sprint {
    private int sprintNummer;
    private LocalDate beginDatum;
    private LocalDate eindDatum;

    /**
     * Checks if a sprint with the given sprint number exists in the database.
     *
     * @param sprintNummer The sprint number to check
     * @return true if the sprint exists, false otherwise
     */
    public static boolean exists(int sprintNummer) {
        if (sprintNummer <= 0) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT COUNT(*) FROM SPRINT WHERE sprintNummer = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sprintNummer);

            // Execute query
            rs = stmt.executeQuery();

            // Return true if count > 0, false otherwise
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking if sprint exists: " + e.getMessage());
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
     * Retrieves a sprint from the database by sprint number.
     * Uses the exists method to first check if the sprint exists.
     *
     * @param sprintNummer The sprint number to look up
     * @return Sprint object if found, null otherwise
     */
    public static Sprint lookup(int sprintNummer) {
        // Validate input
        if (sprintNummer <= 0) {
            return null;
        }

        // First check if the sprint exists using the exists method
        if (!exists(sprintNummer)) {
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT sprintNummer, beginDatum, eindDatum FROM SPRINT WHERE sprintNummer = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sprintNummer);

            // Execute query
            rs = stmt.executeQuery();

            // Check if sprint exists and return it
            if (rs.next()) {
                int nummer = rs.getInt("sprintNummer");
                LocalDate beginDatum = rs.getDate("beginDatum").toLocalDate();
                LocalDate eindDatum = rs.getDate("eindDatum").toLocalDate();
                return new Sprint(nummer, beginDatum, eindDatum);
            }

            // Return null if sprint not found (shouldn't happen since we checked with exists)
            return null;

        } catch (SQLException e) {
            System.out.println("Error retrieving sprint: " + e.getMessage());
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
     * Retrieves all sprints from the database.
     *
     * @return List of all Sprint objects in the database, empty list if none found or if an error occurs
     */
    public static List<Sprint> getAll() {
        List<Sprint> sprints = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select all sprints
            String sql = "SELECT sprintNummer, beginDatum, eindDatum FROM SPRINT";
            stmt = conn.prepareStatement(sql);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of sprints
            while (rs.next()) {
                int nummer = rs.getInt("sprintNummer");
                LocalDate beginDatum = rs.getDate("beginDatum").toLocalDate();
                LocalDate eindDatum = rs.getDate("eindDatum").toLocalDate();

                Sprint sprint = new Sprint(nummer, beginDatum, eindDatum);
                sprints.add(sprint);
            }

            return sprints;

        } catch (SQLException e) {
            System.out.println("Error retrieving all sprints: " + e.getMessage());
            e.printStackTrace();
            return sprints; // Return empty list in case of error
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
     * Saves the current sprint to the database.
     * If the sprint already exists, updates the begin and end dates.
     * If the sprint doesn't exist, creates a new record.
     * Uses the exists method to check existence.
     *
     * @throws IllegalArgumentException if sprintNummer is less than or equal to 0
     * @throws SQLException if a database error occurs
     */
    public void save() throws IllegalArgumentException, SQLException {
        // Validate sprintNummer
        if (this.getSprintNummer() <= 0) {
            throw new IllegalArgumentException("SprintNummer cannot be less than or equal to 0");
        }

        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Check if sprint already exists using the exists method
            boolean sprintExists = exists(this.getSprintNummer());

            // Update or insert based on existence
            if (sprintExists) {
                // Update existing sprint
                String updateSql = "UPDATE SPRINT SET beginDatum = ?, eindDatum = ? WHERE sprintNummer = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDate(1, java.sql.Date.valueOf(this.getBeginDatum()));
                updateStmt.setDate(2, java.sql.Date.valueOf(this.getEindDatum()));
                updateStmt.setInt(3, this.getSprintNummer());
                updateStmt.executeUpdate();
            } else {
                // Insert new sprint
                String insertSql = "INSERT INTO SPRINT (sprintNummer, beginDatum, eindDatum) VALUES (?, ?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, this.getSprintNummer());
                insertStmt.setDate(2, java.sql.Date.valueOf(this.getBeginDatum()));
                insertStmt.setDate(3, java.sql.Date.valueOf(this.getEindDatum()));
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
     * Creates a new Sprint with the specified sprint number, begin date, and end date.
     *
     * @param sprintNummer the sprint number
     * @param beginDatum the begin date of the sprint
     * @param eindDatum the end date of the sprint
     */
    public Sprint(int sprintNummer, LocalDate beginDatum, LocalDate eindDatum) {
        this.sprintNummer = sprintNummer;
        this.beginDatum = beginDatum;
        this.eindDatum = eindDatum;
    }

    /**
     * Deletes this sprint from the database.
     *
     * @return true if the sprint was successfully deleted, false if the sprint didn't exist or an error occurred
     */
    public boolean delete() {
        if (this.getSprintNummer() <= 0) {
            return false;
        }

        // Only attempt to delete if the sprint exists
        if (!exists(this.getSprintNummer())) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Prepare delete statement
            String sql = "DELETE FROM SPRINT WHERE sprintNummer = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.getSprintNummer());

            // Execute delete operation
            int rowsAffected = stmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting sprint: " + e.getMessage());
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

    public int getSprintNummer() {
        return sprintNummer;
    }

    public void setSprintNummer(int sprintNummer) {
        this.sprintNummer = sprintNummer;
    }

    public LocalDate getBeginDatum() {
        return beginDatum;
    }

    public void setBeginDatum(LocalDate beginDatum) {
        this.beginDatum = beginDatum;
    }

    public LocalDate getEindDatum() {
        return eindDatum;
    }

    public void setEindDatum(LocalDate eindDatum) {
        this.eindDatum = eindDatum;
    }
}
