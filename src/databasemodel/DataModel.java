package databasemodel;

import database.Database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a user in the system with username and display name.
 * Provides methods for database operations related to user management.
 */
class Gebruiker {
    private String gebruikersnaam;
    private String weergavenaam;

    /**
     * Checks if a user with the given username exists in the database.
     *
     * @param gebruikersnaam The username to check
     * @return true if the user exists, false otherwise
     */
    public static boolean exists(String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.isEmpty()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT COUNT(*) FROM GEBRUIKER WHERE gebruikersnaam = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, gebruikersnaam);

            // Execute query
            rs = stmt.executeQuery();

            // Return true if count > 0, false otherwise
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking if user exists: " + e.getMessage());
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
     * Retrieves a user from the database by username.
     * Uses the exists method to first check if the user exists.
     *
     * @param gebruikersnaam The username to look up
     * @return Gebruiker object if found, null otherwise
     */
    public static Gebruiker lookup(String gebruikersnaam) {
        // Validate input
        if (gebruikersnaam == null || gebruikersnaam.isEmpty()) {
            return null;
        }

        // First check if the user exists using the exists method
        if (!exists(gebruikersnaam)) {
            return null;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Get database connection from singleton
            conn = Database.getInstance().getConnection();

            // Prepare SQL query with parameter
            String sql = "SELECT gebruikersnaam, weergavenaam FROM GEBRUIKER WHERE gebruikersnaam = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, gebruikersnaam);

            // Execute query
            rs = stmt.executeQuery();

            // Check if user exists and return it
            if (rs.next()) {
                String username = rs.getString("gebruikersnaam");
                String displayName = rs.getString("weergavenaam");
                return new Gebruiker(username, displayName);
            }

            // Return null if user not found (shouldn't happen since we checked with bestaat)
            return null;

        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
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
     * Saves the current user to the database.
     * If the user already exists, updates the display name.
     * If the user doesn't exist, creates a new record.
     * Uses the exists method to check existence.
     *
     * @throws IllegalArgumentException if gebruikersnaam is null or empty
     * @throws SQLException if a database error occurs
     */
    public void save() throws IllegalArgumentException, SQLException {
        // Validate gebruikersnaam
        if (this.getGebruikersnaam() == null || this.getGebruikersnaam().isEmpty()) {
            throw new IllegalArgumentException("Gebruikersnaam cannot be empty or null");
        }

        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Check if user already exists using the exists method
            boolean userExists = exists(this.getGebruikersnaam());

            // Update or insert based on existence
            if (userExists) {
                // Update existing user
                String updateSql = "UPDATE GEBRUIKER SET weergavenaam = ? WHERE gebruikersnaam = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, this.getWeergavenaam());
                updateStmt.setString(2, this.getGebruikersnaam());
                updateStmt.executeUpdate();
            } else {
                // Insert new user
                String insertSql = "INSERT INTO GEBRUIKER (gebruikersnaam, weergavenaam) VALUES (?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, this.getGebruikersnaam());
                insertStmt.setString(2, this.getWeergavenaam());
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
     * Creates a new Gebruiker with the specified username and display name.
     *
     * @param gebruikersnaam the username for the user
     * @param weergavenaam the display name for the user
     */
    public Gebruiker(String gebruikersnaam, String weergavenaam) {
        this.gebruikersnaam = gebruikersnaam;
        this.weergavenaam = weergavenaam;
    }

    /**
     * Deletes this user from the database.
     *
     * @return true if the user was successfully deleted, false if the user didn't exist or an error occurred
     */
    public boolean delete() {
        if (this.getGebruikersnaam() == null || this.getGebruikersnaam().isEmpty()) {
            return false;
        }

        // Only attempt to delete if the user exists
        if (!exists(this.getGebruikersnaam())) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get database connection
            conn = Database.getInstance().getConnection();

            // Prepare delete statement
            String sql = "DELETE FROM GEBRUIKER WHERE gebruikersnaam = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.getGebruikersnaam());

            // Execute delete operation
            int rowsAffected = stmt.executeUpdate();

            // Return true if at least one row was deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
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

    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public String getWeergavenaam() {
        return weergavenaam;
    }

    public void setWeergavenaam(String weergavenaam) {
        this.weergavenaam = weergavenaam;
    }
}

class Sprint {
    private int sprintNummer;
    private LocalDate beginDatum;
    private LocalDate eindDatum;

    public Sprint(int sprintNummer, LocalDate beginDatum, LocalDate eindDatum) {
        this.sprintNummer = sprintNummer;
        this.beginDatum = beginDatum;
        this.eindDatum = eindDatum;
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

class Bericht {
    private int berichtID;
    private String inhoud;
    private LocalDateTime tijdstip;
    private String afzender;
    private int sprintNummer;

    public Bericht(int berichtID, String inhoud, LocalDateTime tijdstip, String afzender, int sprintNummer) {
        this.berichtID = berichtID;
        this.inhoud = inhoud;
        this.tijdstip = tijdstip;
        this.afzender = afzender;
        this.sprintNummer = sprintNummer;
    }

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

class Trello {
    private int trelloID;
    private String trelloURL;

    public Trello(int trelloID, String trelloURL) {
        this.trelloID = trelloID;
        this.trelloURL = trelloURL;
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

class Taak {
    private int berichtID;
    private int trelloID;
    private String beschrijving;

    public Taak(int berichtID, int trelloID, String beschrijving) {
        this.berichtID = berichtID;
        this.trelloID = trelloID;
        this.beschrijving = beschrijving;
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