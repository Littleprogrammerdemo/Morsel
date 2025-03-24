package app.unit_tests.friendships;

import app.follow.model.FriendshipInvitation;
import app.follow.repository.FriendshipInvitationRepository;
import app.follow.service.FriendshipInvitationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceUnitTest {

    @Mock
    private FriendshipInvitationRepository friendshipInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendshipInvitationService friendshipInvitationService;

    private User authenticatedUser;
    private User anotherUser;
    private UUID friendshipId;

    @BeforeEach
    void setUp() {
        // Mock authenticated user
        authenticatedUser = new User();
        authenticatedUser.setId(UUID.randomUUID());

        // Another user
        anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        // Friendship ID
        friendshipId = UUID.randomUUID();

        // Mock AuthenticationMetadata
        AuthenticationMetadata authMetadata = mock(AuthenticationMetadata.class);
        when(authMetadata.getUserId()).thenReturn(authenticatedUser.getId());

        // Mock Authentication interface
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(authMetadata); // Return the mock AuthenticationMetadata

        // Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Set the security context globally
        SecurityContextHolder.setContext(securityContext);

        // Mock user repository behavior
        when(userRepository.findById(authenticatedUser.getId())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));

        // Mock friendshipInvitationRepository methods
        when(friendshipInvitationRepository.findById(friendshipId))
                .thenReturn(Optional.of(new FriendshipInvitation(authenticatedUser, anotherUser)));

        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(authenticatedUser, anotherUser))
                .thenReturn(false);
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(anotherUser, authenticatedUser))
                .thenReturn(false);
    }

    @Test
    void createFriendshipInvitation_Success() {
        // Given: No existing invitation
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(authenticatedUser, anotherUser))
                .thenReturn(false);
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(anotherUser, authenticatedUser))
                .thenReturn(false);

        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, anotherUser);
        when(friendshipInvitationRepository.save(any(FriendshipInvitation.class))).thenReturn(invitation);

        // When
        FriendshipInvitation result = friendshipInvitationService.createFriendshipInvitation(anotherUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(authenticatedUser, result.getInvitingUser());
        assertEquals(anotherUser, result.getAcceptingUser());

        verify(friendshipInvitationRepository, times(1)).save(any(FriendshipInvitation.class));
    }

    @Test
    void createFriendshipInvitation_Fails_SelfInvitation() {
        // When & Then
        assertThrows(IllegalStateException.class, () ->
                friendshipInvitationService.createFriendshipInvitation(authenticatedUser.getId())
        );
    }

    @Test
    void createFriendshipInvitation_Fails_DuplicateRequest() {
        // Given: Already sent or received
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(authenticatedUser, anotherUser))
                .thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                friendshipInvitationService.createFriendshipInvitation(anotherUser.getId())
        );
    }

    @Test
    void findPendingInvitations_ReturnsInvitations() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(anotherUser, authenticatedUser);
        when(friendshipInvitationRepository.findByAcceptingUserAndAcceptedFalse(authenticatedUser))
                .thenReturn(List.of(invitation));

        // When
        List<FriendshipInvitation> result = friendshipInvitationService.findPendingInvitations();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(authenticatedUser, result.get(0).getAcceptingUser());
    }

    @Test
    void findAcceptedFriends_ReturnsFriends() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, anotherUser);
        invitation.setAccepted(true);
        when(friendshipInvitationRepository.findByInvitingUserOrAcceptingUserAndAcceptedTrue(authenticatedUser, authenticatedUser))
                .thenReturn(List.of(invitation));

        // When
        List<FriendshipInvitation> result = friendshipInvitationService.findAcceptedFriends();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAccepted());
    }

    @Test
    void acceptFriendshipInvitation_Success() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(anotherUser, authenticatedUser);
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        // When
        friendshipInvitationService.acceptFriendshipInvitation(friendshipId);

        // Then
        assertTrue(invitation.isAccepted());
        verify(friendshipInvitationRepository).save(invitation);
    }

    @Test
    void acceptFriendshipInvitation_Fails_IfNotReceiver() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, anotherUser);
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                friendshipInvitationService.acceptFriendshipInvitation(friendshipId)
        );
    }

    @Test
    void acceptFriendshipInvitation_Fails_IfAlreadyAccepted() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(anotherUser, authenticatedUser);
        invitation.setAccepted(true);
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                friendshipInvitationService.acceptFriendshipInvitation(friendshipId)
        );
    }

    @Test
    void declineFriendshipInvitation_Success() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(anotherUser, authenticatedUser);
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        // When
        friendshipInvitationService.declineFriendshipInvitation(friendshipId);

        // Then
        verify(friendshipInvitationRepository).deleteById(friendshipId);
    }

    @Test
    void removeFriendship_Success() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, anotherUser);
        invitation.setAccepted(true);
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        // When
        friendshipInvitationService.removeFriendship(friendshipId);

        // Then
        verify(friendshipInvitationRepository).deleteById(friendshipId);
    }

    @Test
    void removeFriendship_Fails_IfNotFriend() {
        // Given
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, anotherUser);
        invitation.setAccepted(true);

        // Mock that authenticated user is **not** in the invitation
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));
        User randomUser = new User();
        randomUser.setId(UUID.randomUUID());
        when(userRepository.findById(randomUser.getId())).thenReturn(Optional.of(randomUser));

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                friendshipInvitationService.removeFriendship(friendshipId)
        );
    }
}
