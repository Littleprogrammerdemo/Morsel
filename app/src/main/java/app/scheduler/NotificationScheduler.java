package app.scheduler;

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

    // Scheduled task for morning (8:00 AM)
    @Scheduled(cron = "0 0 8 * * ?") // Executes at 8:00 AM every day
    public void sendMorningNotifications() {
        sendAutomatedNotifications("Morning notifications");
    }

    // Scheduled task for afternoon (1:00 PM)
    @Scheduled(cron = "0 0 13 * * ?") // Executes at 1:00 PM every day
    public void sendAfternoonNotifications() {
        sendAutomatedNotifications("Afternoon notifications");
    }

    // Scheduled task for night (8:00 PM)
    @Scheduled(cron = "0 0 20 * * ?") // Executes at 8:00 PM every day
    public void sendNightNotifications() {
        sendAutomatedNotifications("Night notifications");
    }

    // General method to send notifications
    public void sendAutomatedNotifications(String message) {
        // Get the list of users with unread notifications
        List<User> usersWithUnreadNotifications = getUsersWithUnreadNotifications();

        // Check if there are users with unread notifications
        if (!usersWithUnreadNotifications.isEmpty()) {
            // Loop through the users and send notifications
            for (User user : usersWithUnreadNotifications) {
                notificationService.sendNotification(user, message, NotificationType.INFO);
                notificationService.sendNotificationByEmail(user, message, NotificationType.INFO);
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
