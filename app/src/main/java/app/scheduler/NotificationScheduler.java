package app.scheduler;

import app.notification.model.Notification;
import app.notification.model.NotificationType;
import app.notification.service.NotificationService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final UserService userService;

    // Constructor injection for NotificationService and UserService
    public NotificationScheduler(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // Scheduled task that runs periodically, but only sends notifications if needed
    @Scheduled(fixedRate = 5000) // Executes every 5 seconds
    public void sendAutomatedNotifications() {
        // Get the list of users with unread notifications
        List<User> usersWithUnreadNotifications = getUsersWithUnreadNotifications();

        // Check if there are users with unread notifications
        if (!usersWithUnreadNotifications.isEmpty()) {
            // Loop through the users and send notifications
            for (User user : usersWithUnreadNotifications) {
                notificationService.sendNotification(user, "Automated message", NotificationType.INFO);
                notificationService.sendNotificationByEmail(user, "Automated message", NotificationType.INFO);
            }
        } else {
            System.out.println("No unread notifications, skipping automated notifications.");
        }
    }

    // Method to define users that have unread notifications
    private List<User> getUsersWithUnreadNotifications() {
        // Fetch all users from the UserService and filter out those with unread notifications
        List<User> users = userService.getAllUsers();
        return users.stream()
                .filter(user -> !notificationService.getUnreadNotifications(user).isEmpty()) // Only users with unread notifications
                .toList();
    }
}
