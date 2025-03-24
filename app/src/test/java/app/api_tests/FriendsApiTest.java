package app.api_tests;

import app.follow.service.FriendshipInvitationService;
import app.security.AuthenticationMetadata;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.controller.FriendshipInvitationController;
import app.web.dto.FriendshipInvitationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendshipInvitationController.class)
class FriendsApiTest {

    @InjectMocks
    private FriendshipInvitationController friendshipInvitationController;

    @MockBean
    private FriendshipInvitationService friendshipInvitationService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private AuthenticationMetadata authenticationMetadata;

    @BeforeEach
    void setUp() {
        authenticationMetadata = new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true);
    }


    @Test
    void shouldSendFriendshipInvitation() throws Exception {
        UUID acceptingUserId = UUID.randomUUID();
        doNothing().when(friendshipInvitationService).createFriendshipInvitation(acceptingUserId);

        mockMvc.perform(post("/friendships/invite")
                        .param("acceptingUserId", acceptingUserId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(authenticationMetadata)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friendships/pending"));

        // Verify the service method was called once
        verify(friendshipInvitationService, times(1)).createFriendshipInvitation(acceptingUserId);
    }

    @Test
    void shouldAcceptFriendshipInvitation() throws Exception {
        UUID friendshipId = UUID.randomUUID();
        doNothing().when(friendshipInvitationService).acceptFriendshipInvitation(friendshipId);

        mockMvc.perform(post("/friendships/accept/{friendshipId}", friendshipId)
                        .with(SecurityMockMvcRequestPostProcessors.user(authenticationMetadata)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friendships/friends"));

        // Verify the service method was called once
        verify(friendshipInvitationService, times(1)).acceptFriendshipInvitation(friendshipId);
    }

    @Test
    void shouldDeclineFriendshipInvitation() throws Exception {
        UUID friendshipId = UUID.randomUUID();
        doNothing().when(friendshipInvitationService).declineFriendshipInvitation(friendshipId);

        mockMvc.perform(post("/friendships/decline/{friendshipId}", friendshipId)
                        .with(SecurityMockMvcRequestPostProcessors.user(authenticationMetadata)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friendships/pending"));

        // Verify the service method was called once
        verify(friendshipInvitationService, times(1)).declineFriendshipInvitation(friendshipId);
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        UUID friendshipId = UUID.randomUUID();
        doNothing().when(friendshipInvitationService).removeFriendship(friendshipId);

        mockMvc.perform(post("/friendships/remove/{friendshipId}", friendshipId)
                        .with(SecurityMockMvcRequestPostProcessors.user(authenticationMetadata)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friendships/friends"));

        // Verify the service method was called once
        verify(friendshipInvitationService, times(1)).removeFriendship(friendshipId);
    }
}
