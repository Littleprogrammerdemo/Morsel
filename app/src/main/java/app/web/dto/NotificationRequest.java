package app.web.dto;

import app.notification.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotBlank(message = "Notification message cannot be empty")
    private String message;

    @NotNull(message = "Notification type cannot be null")
    private NotificationType type;
}
