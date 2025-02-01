package app.web.controller;

import app.notification.model.Notification;
import app.notification.model.NotificationType;
import app.notification.service.NotificationService;
import app.user.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public void sendNotification(@RequestParam UUID userId, @RequestParam String message, @RequestParam NotificationType type) {
        User user = new User();
        user.setId(userId);
        notificationService.sendNotification(user, message, type);
    }

    @GetMapping("/unread/{userId}")
    public List<Notification> getUnreadNotifications(@PathVariable UUID userId) {
        User user = new User();
        user.setId(userId);
        return notificationService.getUnreadNotifications(user);
    }

    @PutMapping("/mark-as-read/{notificationId}")
    public void markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
    }
}
