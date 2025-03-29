package app.unit_tests.friendships;

import app.follow.model.FriendshipInvitation;
import app.follow.service.FriendshipInvitationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.FriendshipInvitationController;
import app.web.dto.FriendshipInvitationResponse;
import app.web.dto.UserResponse;
import app.web.dto.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FriendshipInvitationControllerUnitTest {

    @InjectMocks
    private FriendshipInvitationController friendshipInvitationController;

    @Mock
    private FriendshipInvitationService friendshipInvitationService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    private User authenticatedUser;
    private UUID friendshipId;
    private AuthenticationMetadata auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        friendshipId = UUID.randomUUID();

        // Mock authenticated user
        authenticatedUser = new User();
        authenticatedUser.setId(UUID.randomUUID());
        authenticatedUser.setUsername("JohnDoe");

        auth = mock(AuthenticationMetadata.class);
        when(auth.getUserId()).thenReturn(authenticatedUser.getId());
        when(userService.getByUserId(auth.getUserId())).thenReturn(authenticatedUser);
    }
    @Test
    void shouldShowFriendsSuccessfully() {
        // Mock friends list
        List<FriendshipInvitationResponse> friends = List.of(new FriendshipInvitationResponse());
        when(friendshipInvitationService.findAcceptedFriends(authenticatedUser))
                .thenReturn(List.of());

        // Execute
        String viewName = friendshipInvitationController.showFriends(auth, model);

        // Assertions
        assertEquals("friendship-list", viewName);
        verify(model).addAttribute(eq("friendDetails"), any());
        verify(model).addAttribute("user", authenticatedUser);
    }

    @Test
    void shouldShowInviteFormSuccessfully() {
        // Mock user list
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("User1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("User2");

        List<User> allUsers = List.of(user1, user2);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // Execute
        String viewName = friendshipInvitationController.showInviteForm(auth, model);

        // Assertions
        assertEquals("friendship-invite", viewName);
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void shouldSendFriendshipInvitationSuccessfully() {
        UUID acceptingUserId = UUID.randomUUID();

        // Execute
        String viewName = friendshipInvitationController.sendFriendshipInvitation(auth, acceptingUserId);

        // Assertions
        assertEquals("redirect:/friendships/pending", viewName);
        verify(friendshipInvitationService).createFriendshipInvitation(authenticatedUser, acceptingUserId);
    }

    @Test
    void shouldAcceptFriendshipSuccessfully() {
        // Execute
        String viewName = friendshipInvitationController.acceptFriendship(auth, friendshipId);

        // Assertions
        assertEquals("redirect:/friendships/friends", viewName);
        verify(friendshipInvitationService).acceptFriendshipInvitation(authenticatedUser, friendshipId);
    }

    @Test
    void shouldDeclineFriendshipSuccessfully() {
        // Execute
        String viewName = friendshipInvitationController.declineFriendship(auth, friendshipId);

        // Assertions
        assertEquals("redirect:/friendships/pending", viewName);
        verify(friendshipInvitationService).declineFriendshipInvitation(authenticatedUser, friendshipId);
    }

    @Test
    void shouldRemoveFriendSuccessfully() {
        // Execute
        String viewName = friendshipInvitationController.removeFriend(auth, friendshipId);

        // Assertions
        assertEquals("redirect:/friendships/friends", viewName);
        verify(friendshipInvitationService).removeFriendship(authenticatedUser, friendshipId);
    }
}
