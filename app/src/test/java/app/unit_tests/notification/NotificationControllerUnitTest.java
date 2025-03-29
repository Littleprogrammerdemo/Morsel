package app.unit_tests.notification;

import app.notification.client.dto.*;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.NotificationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationControllerUnitTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthenticationMetadata authenticationMetadata;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a test user
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("TestUser");

        when(authenticationMetadata.getUserId()).thenReturn(userId);
        when(userService.getByUserId(userId)).thenReturn(testUser);
    }

    @Test
    void shouldUpdateUserPreferenceSuccessfully() {
        // Execute
        String redirectUrl = notificationController.updateUserPreference(true, authenticationMetadata);

        // Assertions
        assertEquals("redirect:/notifications", redirectUrl);
        verify(notificationService).updateNotificationPreference(userId, true);
    }

    @Test
    void shouldDeleteNotificationHistorySuccessfully() {
        // Execute
        String redirectUrl = notificationController.deleteNotificationHistory(authenticationMetadata);

        // Assertions
        assertEquals("redirect:/notifications", redirectUrl);
        verify(notificationService).clearHistory(userId);
    }

    @Test
    void shouldRetryFailedNotificationsSuccessfully() {
        // Execute
        String redirectUrl = notificationController.retryFailedNotifications(authenticationMetadata);

        // Assertions
        assertEquals("redirect:/notifications", redirectUrl);
        verify(notificationService).retryFailed(userId);
    }

    @Test
    void shouldHandleLikeNotificationSuccessfully() {
        // Create LikeRequest mock
        LikeRequest likeRequest = new LikeRequest(UUID.randomUUID(), userId);

        // Execute
        notificationController.likePost(likeRequest);

        // Verify interaction
        verify(notificationService).handleLikeNotification(likeRequest);
    }

    @Test
    void shouldHandleCommentNotificationSuccessfully() {
        // Create CommentRequest mock
        CommentRequest commentRequest = new CommentRequest(UUID.randomUUID(), userId, "Nice post!");

        // Execute
        notificationController.postComment(commentRequest);

        // Verify interaction
        verify(notificationService).handleCommentNotification(commentRequest);
    }

    @Test
    void shouldHandleFriendRequestNotificationSuccessfully() {
        // Create FriendRequest mock
        FriendRequest friendRequest = new FriendRequest(UUID.randomUUID(), userId);

        // Execute
        notificationController.sendFriendRequest(friendRequest);

        // Verify interaction
        verify(notificationService).handleFriendRequestNotification(friendRequest);
    }

}
