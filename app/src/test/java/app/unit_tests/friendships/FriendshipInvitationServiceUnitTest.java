package app.unit_tests.friendships;

import app.follow.model.FriendshipInvitation;
import app.follow.repository.FriendshipInvitationRepository;
import app.follow.service.FriendshipInvitationService;
import app.user.model.User;
import app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendshipInvitationServiceUnitTest {

    @InjectMocks
    private FriendshipInvitationService friendshipInvitationService;

    @Mock
    private FriendshipInvitationRepository friendshipInvitationRepository;

    @Mock
    private UserRepository userRepository;

    private User authenticatedUser;
    private User acceptingUser;
    private UUID friendshipId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock users
        authenticatedUser = new User();
        authenticatedUser.setId(UUID.randomUUID());
        authenticatedUser.setUsername("JohnDoe");

        acceptingUser = new User();
        acceptingUser.setId(UUID.randomUUID());
        acceptingUser.setUsername("JaneDoe");

        friendshipId = UUID.randomUUID();
    }

    @Test
    void shouldCreateFriendshipInvitationSuccessfully() {
        when(userRepository.findById(acceptingUser.getId())).thenReturn(Optional.of(acceptingUser));
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(authenticatedUser, acceptingUser)).thenReturn(false);
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(acceptingUser, authenticatedUser)).thenReturn(false);
        when(friendshipInvitationRepository.save(any(FriendshipInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FriendshipInvitation invitation = friendshipInvitationService.createFriendshipInvitation(authenticatedUser, acceptingUser.getId());

        assertNotNull(invitation);
        assertEquals(authenticatedUser, invitation.getInvitingUser());
        assertEquals(acceptingUser, invitation.getAcceptingUser());

        verify(friendshipInvitationRepository).save(any(FriendshipInvitation.class));
    }

    @Test
    void shouldThrowExceptionWhenSendingInvitationToSelf() {
        assertThrows(IllegalStateException.class, () -> friendshipInvitationService.createFriendshipInvitation(authenticatedUser, authenticatedUser.getId()));
    }

    @Test
    void shouldNotCreateDuplicateFriendshipInvitation() {
        when(friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(authenticatedUser, acceptingUser)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> friendshipInvitationService.createFriendshipInvitation(authenticatedUser, acceptingUser.getId()));

        verify(friendshipInvitationRepository, never()).save(any(FriendshipInvitation.class));
    }

    @Test
    void shouldFindPendingInvitations() {
        when(friendshipInvitationRepository.findByAcceptingUserAndAcceptedFalse(authenticatedUser))
                .thenReturn(List.of(new FriendshipInvitation(authenticatedUser, acceptingUser)));

        List<FriendshipInvitation> invitations = friendshipInvitationService.findPendingInvitations(authenticatedUser);

        assertFalse(invitations.isEmpty());
        verify(friendshipInvitationRepository).findByAcceptingUserAndAcceptedFalse(authenticatedUser);
    }

    @Test
    void shouldAcceptFriendshipInvitation() {
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, acceptingUser);
        invitation.setId(friendshipId);

        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        friendshipInvitationService.acceptFriendshipInvitation(acceptingUser, friendshipId);

        assertTrue(invitation.isAccepted());
        verify(friendshipInvitationRepository).save(invitation);
    }

    @Test
    void shouldThrowExceptionWhenAcceptingNonExistentInvitation() {
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> friendshipInvitationService.acceptFriendshipInvitation(acceptingUser, friendshipId));

        verify(friendshipInvitationRepository, never()).save(any());
    }

    @Test
    void shouldDeclineFriendshipInvitation() {
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, acceptingUser);
        invitation.setId(friendshipId);

        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        friendshipInvitationService.declineFriendshipInvitation(acceptingUser, friendshipId);

        verify(friendshipInvitationRepository).deleteById(friendshipId);
    }

    @Test
    void shouldThrowExceptionWhenDecliningNonExistentInvitation() {
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> friendshipInvitationService.declineFriendshipInvitation(acceptingUser, friendshipId));

        verify(friendshipInvitationRepository, never()).deleteById(any());
    }

    @Test
    void shouldRemoveFriendship() {
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, acceptingUser);
        invitation.setId(friendshipId);
        invitation.setAccepted(true);

        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        friendshipInvitationService.removeFriendship(authenticatedUser, friendshipId);

        verify(friendshipInvitationRepository).deleteById(friendshipId);
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentFriendship() {
        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> friendshipInvitationService.removeFriendship(authenticatedUser, friendshipId));

        verify(friendshipInvitationRepository, never()).deleteById(any());
    }

    @Test
    void shouldNotAcceptFriendshipInvitationIfUnauthorized() {
        FriendshipInvitation invitation = new FriendshipInvitation(authenticatedUser, acceptingUser);
        invitation.setId(friendshipId);

        when(friendshipInvitationRepository.findById(friendshipId)).thenReturn(Optional.of(invitation));

        assertThrows(IllegalStateException.class, () -> friendshipInvitationService.acceptFriendshipInvitation(new User(), friendshipId));
    }
}
