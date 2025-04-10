package model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String content;
    private User sender;
    private LocalDateTime timestamp;
    private String trelloUrl;
    private int sprintNumber;

    public Message(String content, User sender, String trelloUrl, int sprintNumber) {
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.trelloUrl = trelloUrl;
        this.sprintNumber = sprintNumber;
    }

    // Constructor with ID for database retrieval
    public Message(int id, String content, User sender, LocalDateTime timestamp,
                   String trelloUrl, int sprintNumber) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.trelloUrl = trelloUrl;
        this.sprintNumber = sprintNumber;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getSender() {
        return sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTrelloUrl() {
        return trelloUrl;
    }

    public int getSprintNumber() {
        return sprintNumber;
    }

    public String getFormattedMetadata() {
        return String.format("[%s] by %s at %s (Sprint %d)",
                id, sender.getUsername(), timestamp.toString(), sprintNumber);
    }
}