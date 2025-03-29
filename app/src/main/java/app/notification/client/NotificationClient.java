package app.notification.client;

import app.notification.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1/notifications")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreference notificationPreference);

    @GetMapping("/preferences")
    ResponseEntity<NotificationPreference> getUserPreference(@RequestParam(name = "userId") UUID userId);

    @GetMapping
    ResponseEntity<List<Notification>> getNotificationHistory(@RequestParam(name = "userId")UUID userId);

    @PostMapping
    ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest notificationRequest);

    @PutMapping("/preferences")
    ResponseEntity<Void> updateNotificationPreference(@RequestParam("userId") UUID userId, @RequestParam("enabled") boolean enabled);

    @DeleteMapping
    ResponseEntity<Void> clearHistory(@RequestParam(name = "userId") UUID userId);

    @PutMapping
    ResponseEntity<Void> retryFailedNotifications(@RequestParam(name = "userId") UUID userId);

    @PostMapping("/like")
    ResponseEntity<Void> sendLikeNotification(@RequestBody LikeRequest likeRequest);

    // Sending a comment notification
    @PostMapping("/comment")
    ResponseEntity<Void> sendCommentNotification(@RequestBody CommentRequest commentRequest);

    @PostMapping("/friend-request")
    ResponseEntity<Void> sendFriendRequestNotification(@RequestBody FriendRequest friendRequest);

    @PostMapping("/rating")
    ResponseEntity<Void> sendRatingRequestNotification(@RequestBody RatingRequest ratingRequest);

}