package app.message.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String sender;
    private String receiver;

    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Automatically set timestamp
    }
}
