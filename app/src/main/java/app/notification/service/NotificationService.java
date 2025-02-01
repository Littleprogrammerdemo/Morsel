package app.notification.service;

import app.notification.model.Notification;
import app.notification.model.NotificationType;
import app.notification.repository.NotificationRepository;
import app.user.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket Messaging

    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        // Send WebSocket Notification
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(), "/queue/notifications", notification
        );
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
