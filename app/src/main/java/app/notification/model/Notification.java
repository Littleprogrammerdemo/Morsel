package app.notification.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Потребител, който ще получи известието

    private String message;
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private LocalDateTime timestamp = LocalDateTime.now();

}
