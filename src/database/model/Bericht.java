package database.model;

import database.Database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message in the system with content, timestamp, sender and sprint number.
 * Provides methods for database operations related to message management.
 */
class Bericht {
    private int berichtID;
    private String inhoud;
    private LocalDateTime tijdstip;
    private String afzender;
    private int sprintNummer;

    /**
     * Creates a new Bericht with the specified ID, content, timestamp, sender and sprint number.
     *
     * @param berichtID the unique identifier for the message
     * @param inhoud the content of the message
     * @param tijdstip the timestamp when the message was created
     * @param afzender the username of the sender
     * @param sprintNummer the sprint number this message belongs to
     */
    public Bericht(int berichtID, String inhoud, LocalDateTime tijdstip, String afzender, int sprintNummer) {
        this.berichtID = berichtID;
        this.inhoud = inhoud;
        this.tijdstip = tijdstip;
        this.afzender = afzender;
        this.sprintNummer = sprintNummer;
    }

    /**
     * Checks if a message with the given ID exists in the database.
     *
     * @param berichtID The message ID to check
     * @return true if the message exists, false otherwise
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
            String sql = "SELECT COUNT(*) FROM BERICHT WHERE berichtID = ?";
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
            System.out.println("Error checking if message exists: " + e.getMessage());
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
     * Retrieves a message from the database by ID.
     * Uses the exists method to first check if the message exists.
     *
     * @param berichtID The message ID to look up
     * @return Bericht object if found, null otherwise
     */
    public static Bericht lookup(int berichtID) {
        // Validate input
        if (berichtID <= 0) {
            return null;
        }

        // First check if the message exists using the exists method
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
            String sql = "SELECT berichtID, inhoud, tijdstip, afzender, sprintNummer FROM BERICHT WHERE berichtID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, berichtID);

            // Execute query
            rs = stmt.executeQuery();

            // Check if message exists and return it
            if (rs.next()) {
                int id = rs.getInt("berichtID");
                String inhoud = rs.getString("inhoud");
                LocalDateTime tijdstip = rs.getTimestamp("tijdstip").toLocalDateTime();
                String afzender = rs.getString("afzender");
                int sprintNummer = rs.getInt("sprintNummer");

                return new Bericht(id, inhoud, tijdstip, afzender, sprintNummer);
            }

            // Return null if message not found (shouldn't happen since we checked with exists)
            return null;

        } catch (SQLException e) {
            System.out.println("Error retrieving message: " + e.getMessage());
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
     * Retrieves all messages from the database.
     *
     * @return List of all Bericht objects in the database, empty list if none found or if an error occurs
     */
    public static List<Bericht> getAll() {
        List<Bericht> berichten = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select all messages
            String sql = "SELECT berichtID, inhoud, tijdstip, afzender, sprintNummer FROM BERICHT";
            stmt = conn.prepareStatement(sql);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of berichten
            while (rs.next()) {
                int id = rs.getInt("berichtID");
                String inhoud = rs.getString("inhoud");
                LocalDateTime tijdstip = rs.getTimestamp("tijdstip").toLocalDateTime();
                String afzender = rs.getString("afzender");
                int sprintNummer = rs.getInt("sprintNummer");

                Bericht bericht = new Bericht(id, inhoud, tijdstip, afzender, sprintNummer);
                berichten.add(bericht);
            }

            return berichten;

        } catch (SQLException e) {
            System.out.println("Error retrieving all messages: " + e.getMessage());
            e.printStackTrace();
            return berichten; // Return empty list in case of error
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
     * Retrieves all messages from a specific sender.
     *
     * @param afzender The username of the sender
     * @return List of Bericht objects from the specified sender, empty list if none found or if an error occurs
     */
    public static List<Bericht> getByAfzender(String afzender) {
        List<Bericht> berichten = new ArrayList<>();

        // Validate input
        if (afzender == null || afzender.isEmpty()) {
            return berichten;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select messages by sender
            String sql = "SELECT berichtID, inhoud, tijdstip, afzender, sprintNummer FROM BERICHT WHERE afzender = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, afzender);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of berichten
            while (rs.next()) {
                int id = rs.getInt("berichtID");
                String inhoud = rs.getString("inhoud");
                LocalDateTime tijdstip = rs.getTimestamp("tijdstip").toLocalDateTime();
                String sender = rs.getString("afzender");
                int sprintNummer = rs.getInt("sprintNummer");

                Bericht bericht = new Bericht(id, inhoud, tijdstip, sender, sprintNummer);
                berichten.add(bericht);
            }

            return berichten;

        } catch (SQLException e) {
            System.out.println("Error retrieving messages by sender: " + e.getMessage());
            e.printStackTrace();
            return berichten; // Return empty list in case of error
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
     * Retrieves all messages from a specific sprint.
     *
     * @param sprintNummer The sprint number
     * @return List of Bericht objects from the specified sprint, empty list if none found or if an error occurs
     */
    public static List<Bericht> getBySprint(int sprintNummer) {
        List<Bericht> berichten = new ArrayList<>();

        // Validate input
        if (sprintNummer <= 0) {
            return berichten;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query to select messages by sprint
            String sql = "SELECT berichtID, inhoud, tijdstip, afzender, sprintNummer FROM BERICHT WHERE sprintNummer = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sprintNummer);

            // Execute query
            rs = stmt.executeQuery();

            // Process result set and build list of berichten
            while (rs.next()) {
                int id = rs.getInt("berichtID");
                String inhoud = rs.getString("inhoud");
                LocalDateTime tijdstip = rs.getTimestamp("tijdstip").toLocalDateTime();
                String afzender = rs.getString("afzender");
                int sprint = rs.getInt("sprintNummer");

                Bericht bericht = new Bericht(id, inhoud, tijdstip, afzender, sprint);
                berichten.add(bericht);
            }

            return berichten;

        } catch (SQLException e) {
            System.out.println("Error retrieving messages by sprint: " + e.getMessage());
            e.printStackTrace();
            return berichten; // Return empty list in case of error
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
     * Saves the current message to the database.
     * If the message already exists (berichtID > 0 and exists in DB), updates its content, timestamp, sender and sprint number.
     * If the message doesn't exist, creates a new record.
     * Uses the exists method to check existence.
     *
     * @throws IllegalArgumentException if inhoud is null or empty, or if afzender is null or empty
     * @throws SQLException if a database error occurs
     * @return The berichtID of the saved message (useful when creating new messages with auto-generated IDs)
     */
    public int save() throws IllegalArgumentException, SQLException {
        // Validate input
        if (this.getInhoud() == null || this.getInhoud().isEmpty()) {
            throw new IllegalArgumentException("Inhoud cannot be empty or null");
        }

        if (this.getAfzender() == null || this.getAfzender().isEmpty()) {
            throw new IllegalArgumentException("Afzender cannot be empty or null");
        }

        if (this.getSprintNummer() <= 0) {
            throw new IllegalArgumentException("SprintNummer must be greater than 0");
        }

        if (this.getTijdstip() == null) {
            this.setTijdstip(LocalDateTime.now()); // Set current time if not provided
        }

        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet generatedKeys = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            boolean messageExists = this.getBerichtID() > 0 && exists(this.getBerichtID());

            // Update or insert based on existence
            if (messageExists) {
                // Update existing message
                String updateSql = "UPDATE BERICHT SET inhoud = ?, tijdstip = ?, afzender = ?, sprintNummer = ? WHERE berichtID = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, this.getInhoud());
                updateStmt.setTimestamp(2, Timestamp.valueOf(this.getTijdstip()));
                updateStmt.setString(3, this.getAfzender());
                updateStmt.setInt(4, this.getSprintNummer());
                updateStmt.setInt(5, this.getBerichtID());
                updateStmt.executeUpdate();

                return this.getBerichtID(); // Return existing ID

            } else {
                // Insert new message
                String insertSql = "INSERT INTO BERICHT (inhoud, tijdstip, afzender, sprintNummer) VALUES (?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, this.getInhoud());
                insertStmt.setTimestamp(2, Timestamp.valueOf(this.getTijdstip()));
                insertStmt.setString(3, this.getAfzender());
                insertStmt.setInt(4, this.getSprintNummer());
                insertStmt.executeUpdate();

                // Get the auto-generated ID
                generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    this.setBerichtID(newId); // Update the object with the new ID
                    return newId;
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }

        } finally {
            // Close resources
            try {
                if (generatedKeys != null) generatedKeys.close();
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
     * Deletes this message from the database.
     *
     * @return true if the message was successfully deleted, false if the message didn't exist or an error occurred
     */
    public boolean delete() {
        if (this.getBerichtID() <= 0) {
            return false;
        }

        // Only attempt to delete if the message exists
        if (!exists(this.getBerichtID())) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Prepare delete statement
            String sql = "DELETE FROM BERICHT WHERE berichtID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.getBerichtID());

            // Execute delete operation
            int rowsAffected = stmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting message: " + e.getMessage());
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

    // Getters and setters
    public int getBerichtID() {
        return berichtID;
    }

    public void setBerichtID(int berichtID) {
        this.berichtID = berichtID;
    }

    public String getInhoud() {
        return inhoud;
    }

    public void setInhoud(String inhoud) {
        this.inhoud = inhoud;
    }

    public LocalDateTime getTijdstip() {
        return tijdstip;
    }

    public void setTijdstip(LocalDateTime tijdstip) {
        this.tijdstip = tijdstip;
    }

    public String getAfzender() {
        return afzender;
    }

    public void setAfzender(String afzender) {
        this.afzender = afzender;
    }

    public int getSprintNummer() {
        return sprintNummer;
    }

    public void setSprintNummer(int sprintNummer) {
        this.sprintNummer = sprintNummer;
    }
}