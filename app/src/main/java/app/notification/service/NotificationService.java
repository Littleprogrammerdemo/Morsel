package app.notification.service;

import app.notification.model.Notification;
import app.notification.model.NotificationType;
import app.notification.repository.NotificationRepository;
import app.user.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender emailSender;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket Messaging

    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate, JavaMailSender emailSender) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailSender = emailSender;
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

    public void sendNotificationByEmail(User user, String message, NotificationType type) {
        // First, check if the user has an email address
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            // If there's no email, log it and skip the notification
            System.out.println("No email for user " + user.getUsername() + ". Skipping notification.");
            return;  // Skip sending email if the user doesn't have an email address
        }

        // Create a simple email message
        SimpleMailMessage emailMessage = new SimpleMailMessage();

        // Set the email details
        emailMessage.setTo(user.getEmail());
        emailMessage.setSubject("Notification: " + type);  // Subject based on the notification type
        emailMessage.setText(message);  // Body of the email

        // Send the email
        emailSender.send(emailMessage);
        System.out.println("Email sent to " + user.getEmail() + " with message: " + message);
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
