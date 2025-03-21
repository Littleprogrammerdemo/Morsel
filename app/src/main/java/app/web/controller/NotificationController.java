package app.web.controller;

import app.notification.client.dto.*;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {

        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ModelAndView getNotificationPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getByUserId(authenticationMetadata.getUserId());

        NotificationPreference notificationPreference = notificationService.getNotificationPreference(user.getId());
        List<Notification> notificationHistory = notificationService.getNotificationHistory(user.getId());
        long succeededNotificationsNumber = notificationHistory.stream().filter(notification -> notification.getStatus().equals("SUCCEEDED")).count();
        long failedNotificationsNumber = notificationHistory.stream().filter(notification -> notification.getStatus().equals("FAILED")).count();
        notificationHistory = notificationHistory.stream().limit(5).toList();

        ModelAndView modelAndView = new ModelAndView("notifications");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreference", notificationPreference);
        modelAndView.addObject("succeededNotificationsNumber", succeededNotificationsNumber);
        modelAndView.addObject("failedNotificationsNumber", failedNotificationsNumber);
        modelAndView.addObject("notificationHistory", notificationHistory);

        return modelAndView;
    }

    @PutMapping("/user-preference")
    public String updateUserPreference(@RequestParam(name = "enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        notificationService.updateNotificationPreference(authenticationMetadata.getUserId(), enabled);

        return "redirect:/notifications";
    }

    @DeleteMapping
    public String deleteNotificationHistory(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        notificationService.clearHistory(userId);

        return "redirect:/notifications";
    }

    @PutMapping
    public String retryFailedNotifications(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        notificationService.retryFailed(userId);

        return "redirect:/notifications";
    }
    @PostMapping("/like")
    public void likePost(@RequestBody LikeRequest likeRequestDTO) {
        notificationService.handleLikeNotification(likeRequestDTO);
    }

    // Comments endpoints
    @PostMapping("/comment")
    public void postComment(@RequestBody CommentRequest commentRequestDTO) {
        notificationService.handleCommentNotification(commentRequestDTO);
    }

    // Friend request endpoints
    @PostMapping("/friend-request")
    public void sendFriendRequest(@RequestBody FriendRequest friendRequestDTO) {
        notificationService.handleFriendRequestNotification(friendRequestDTO);
    }
    @PostMapping("/rating")
    public void sendRatingNotification(@RequestBody RatingRequest ratingRequest) {
        notificationService.handleRatingNotification(ratingRequest);
    }
}