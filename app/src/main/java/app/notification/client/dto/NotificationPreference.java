package app.notification.client.dto;

import lombok.Data;

import java.util.UUID;


@Data
public class NotificationPreference {
    private UUID userId;
    private String type;

    private boolean notificationEnabled;

    private String contactInfo;

    // Constructor
    public NotificationPreference(UUID userId, String contactInfo, boolean notificationEnabled) {
        this.userId = userId;
        this.contactInfo = contactInfo;
        this.notificationEnabled = notificationEnabled;
    }
}