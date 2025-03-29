package app.unit_tests.mapper;

import app.follow.model.FriendshipInvitation;
import app.user.model.User;
import app.web.dto.FriendshipInvitationResponse;
import app.web.dto.UserEditRequest;
import app.web.dto.mapper.DtoMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DtoMapperUnitTest {

    @Test
    void shouldMapUserToUserEditRequest() {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setProfilePicture("http://example.com/profile.jpg");

        // Act
        UserEditRequest result = DtoMapper.mapUserToUserEditRequest(user);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("http://example.com/profile.jpg", result.getProfilePicture());
    }

    @Test
    void shouldMapFriendshipInvitationToFriendshipInvitationResponse() {
        // Arrange
        User invitingUser = mock(User.class);
        User acceptingUser = mock(User.class);
        FriendshipInvitation invitation = new FriendshipInvitation();

        // Change to UUID instead of long
        UUID invitationId = UUID.randomUUID();
        invitation.setId(invitationId);
        invitation.setAccepted(true);
        invitation.setInvitingUser(invitingUser);
        invitation.setAcceptingUser(acceptingUser);

        UUID invitingUserId = UUID.randomUUID();
        UUID acceptingUserId = UUID.randomUUID();

        when(invitingUser.getId()).thenReturn(invitingUserId);
        when(invitingUser.getUsername()).thenReturn("InvitingUser");
        when(acceptingUser.getId()).thenReturn(acceptingUserId);
        when(acceptingUser.getUsername()).thenReturn("AcceptingUser");

        // Act
        FriendshipInvitationResponse result = DtoMapper.mapToFriendship(invitation);

        // Assert
        assertNotNull(result);
        assertEquals(invitationId, result.getId());
        assertEquals(invitingUserId, result.getInvitingUserId());
        assertEquals("InvitingUser", result.getInvitingUsername());
        assertEquals(acceptingUserId, result.getAcceptingUserId());
        assertEquals("AcceptingUser", result.getAcceptingUsername());
        assertTrue(result.isAccepted());
    }
}
