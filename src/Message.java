import java.time.LocalDateTime;

public class Message {
    private int id;
    private String content;
    private User sender;
    private LocalDateTime timestamp;
    private String trelloUrl;
    private int sprintNumber;

    public Message(String content, User sender, LocalDateTime timestamp, String trelloUrl, int sprintnummer) {
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.trelloUrl = trelloUrl;
        this.sprintNumber = sprintnummer;
    }

    public Message(int id, String content, User sender, LocalDateTime timestamp, String trelloUrl, int sprintnummer) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.trelloUrl = trelloUrl;
        this.sprintNumber = sprintnummer;
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
}
