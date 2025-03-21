package app.notification.service;

import app.exception.NotificationFeignCallException;
import app.notification.client.NotificationClient;
import app.notification.client.dto.Notification;
import app.notification.client.dto.NotificationPreference;
import app.notification.client.dto.NotificationRequest;
import app.notification.client.dto.UpsertNotificationPreference;
import app.notification.client.dto.Like;
import app.notification.client.dto.Comment;
import app.notification.client.dto.FriendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    @Value("${notification-svc.failure-message.clear-history}")
    private String clearHistoryFailedMessage;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    // Save Notification Preference
    public void saveNotificationPreference(UUID userId, boolean isEmailEnabled, String email) {

        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo(email)
                .type("EMAIL")
                .notificationEnabled(isEmailEnabled)
                .build();

        try {
            ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(notificationPreference);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to notification-svc failed] Can't save user preference for user with id = [%s]".formatted(userId));
            }
        } catch (Exception e) {
            log.error("Unable to call notification-svc.");
        }
    }

    // Get Notification Preference
    public NotificationPreference getNotificationPreference(UUID userId) {
        ResponseEntity<NotificationPreference> httpResponse = notificationClient.getUserPreference(userId);

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Notification preference for user id [%s] does not exist.".formatted(userId));
        }

        return httpResponse.getBody();
    }

    // Get Notification History
    public List<Notification> getNotificationHistory(UUID userId) {
        ResponseEntity<List<Notification>> httpResponse = notificationClient.getNotificationHistory(userId);
        return httpResponse.getBody();
    }

    // Send Notification
    public void sendNotification(UUID userId, String emailSubject, String emailBody) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .userId(userId)
                .subject(emailSubject)
                .body(emailBody)
                .build();

        // Service to Service Communication
        ResponseEntity<Void> httpResponse;
        try {
            httpResponse = notificationClient.sendNotification(notificationRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to notification-svc failed] Can't send email to user with id = [%s]".formatted(userId));
            }
        } catch (Exception e) {
            log.warn("Can't send email to user with id = [%s] due to 500 Internal Server Error.".formatted(userId));
        }
    }

    // Update Notification Preferences
    public void updateNotificationPreference(UUID userId, boolean enabled) {
        try {
            notificationClient.updateNotificationPreference(userId, enabled);
        } catch (Exception e) {
            log.warn("Can't update notification preferences for user with id = [%s].".formatted(userId));
        }
    }

    // Clear Notification History
    public void clearHistory(UUID userId) {
        try {
            notificationClient.clearHistory(userId);
        } catch (Exception e) {
            log.error("Unable to call notification-svc for clear notification history.".formatted(userId));
            throw new NotificationFeignCallException(clearHistoryFailedMessage);
        }
    }

    // Retry Failed Notifications
    public void retryFailed(UUID userId) {
        try {
            notificationClient.retryFailedNotifications(userId);
        } catch (Exception e) {
            log.error("Unable to call notification-svc for retry failed notifications.".formatted(userId));
            throw new NotificationFeignCallException(clearHistoryFailedMessage);
        }
    }

    // Notify User of a New Like
    public void handleLikeNotification(Like likeDTO) {
        String emailSubject = "New Like on Your Post!";
        String emailBody = String.format("User %s liked your post with ID: %s.", likeDTO.getUserId(), likeDTO.getPostId());

        sendNotification(likeDTO.getUserId(), emailSubject, emailBody);
    }

    // Notify User of a New Comment
    public void handleCommentNotification(Comment commentDTO) {
        String emailSubject = "New Comment on Your Post!";
        String emailBody = String.format("User %s commented on your post with ID: %s. Comment: %s",
                commentDTO.getUserId(), commentDTO.getPostId(), commentDTO.getContent());

        sendNotification(commentDTO.getUserId(), emailSubject, emailBody);
    }

    // Notify User of a New Friend Request
    public void handleFriendRequestNotification(FriendRequest friendRequestDTO) {
        String emailSubject = "You Have a New Friend Request!";
        String emailBody = String.format("User %s has sent you a friend request.", friendRequestDTO.getSenderId());

        sendNotification(friendRequestDTO.getReceiverId(), emailSubject, emailBody);
    }
}
