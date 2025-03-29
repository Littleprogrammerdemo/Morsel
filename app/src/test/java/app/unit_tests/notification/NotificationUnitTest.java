package app.unit_tests.notification;

import app.notification.client.NotificationClient;
import app.notification.client.dto.*;
import app.notification.service.NotificationService;
import app.exception.NotificationFeignCallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceUnitTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private String email;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock data
        userId = UUID.randomUUID();
        email = "user@example.com";
    }

    @Test
    void givenValidNotificationPreference_whenSaveNotificationPreference_thenSuccess() {
        // Arrange
        UpsertNotificationPreference preference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo(email)
                .type("EMAIL")
                .notificationEnabled(true)
                .build();

        // Mock the response from notificationClient
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(notificationClient.upsertNotificationPreference(preference)).thenReturn(responseEntity);

        // Act
        notificationService.saveNotificationPreference(userId, true, email);

        // Assert
        verify(notificationClient, times(1)).upsertNotificationPreference(preference);
    }

    @Test
    void givenFailedResponse_whenSaveNotificationPreference_thenHandleFailure() {
        // Arrange
        UpsertNotificationPreference preference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo(email)
                .type("EMAIL")
                .notificationEnabled(true)
                .build();

        // Mock a failed response
        ResponseEntity<Void> responseEntity = ResponseEntity.status(500).build();
        when(notificationClient.upsertNotificationPreference(preference)).thenReturn(responseEntity);

        // Act
        notificationService.saveNotificationPreference(userId, true, email);

        // Assert
        verify(notificationClient, times(1)).upsertNotificationPreference(preference);
    }

    @Test
    void givenExistingNotificationPreference_whenGetNotificationPreference_thenReturnNotificationPreference() {
        // Arrange
        NotificationPreference notificationPreference = new NotificationPreference(userId, "EMAIL", true);
        ResponseEntity<NotificationPreference> responseEntity = ResponseEntity.ok(notificationPreference);
        when(notificationClient.getUserPreference(userId)).thenReturn(responseEntity);

        // Act
        NotificationPreference result = notificationService.getNotificationPreference(userId);

        // Assert
        assertEquals(notificationPreference, result);
    }

    @Test
    void givenNoNotificationPreference_whenGetNotificationPreference_thenThrowException() {
        // Arrange
        ResponseEntity<NotificationPreference> responseEntity = ResponseEntity.status(404).build();
        when(notificationClient.getUserPreference(userId)).thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationService.getNotificationPreference(userId);
        });

        assertEquals("Notification preference for user id [%s] does not exist.".formatted(userId), exception.getMessage());
    }
    @Test
    void givenValidLikeRequest_whenHandleLikeNotification_thenSendLikeNotification() {
        // Arrange
        LikeRequest likeRequestDTO = new LikeRequest(userId, UUID.randomUUID());

        // Act
        notificationService.handleLikeNotification(likeRequestDTO);

        // Assert
        String emailSubject = "New Like on Your Post!";
        String emailBody = String.format("User %s liked your post with ID: %s.", likeRequestDTO.getUserId(), likeRequestDTO.getPostId());
        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void givenValidCommentRequest_whenHandleCommentNotification_thenSendCommentNotification() {
        // Arrange
        CommentRequest commentRequestDTO = new CommentRequest(userId, UUID.randomUUID(), "This is a comment.");

        // Act
        notificationService.handleCommentNotification(commentRequestDTO);

        // Assert
        String emailSubject = "New Comment on Your Post!";
        String emailBody = String.format("User %s commented on your post with ID: %s. Comment: %s", commentRequestDTO.getUserId(), commentRequestDTO.getPostId(), commentRequestDTO.getContent());
        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void givenValidFriendRequest_whenHandleFriendRequestNotification_thenSendFriendRequestNotification() {
        // Arrange
        FriendRequest friendRequestDTO = new FriendRequest(UUID.randomUUID(), userId);

        // Act
        notificationService.handleFriendRequestNotification(friendRequestDTO);

        // Assert
        String emailSubject = "You Have a New Friend Request!";
        String emailBody = String.format("User %s has sent you a friend request.", friendRequestDTO.getSenderId());
        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void givenValidRatingRequest_whenHandleRatingNotification_thenSendRatingNotification() {
        // Arrange
        RatingRequest ratingRequestDTO = new RatingRequest(UUID.randomUUID(), userId, UUID.randomUUID(), 5);

        // Act
        notificationService.handleRatingNotification(ratingRequestDTO);

        // Assert
        String emailSubject = "New Rating on Your Post!";
        String emailBody = String.format("User %s rated your post with ID: %s. Rating: %d.", ratingRequestDTO.getRaterId(), ratingRequestDTO.getPostId(), ratingRequestDTO.getRatingValue());
        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }
}
