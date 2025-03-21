package app.web.dto.mapper;

import app.follow.model.FriendshipInvitation;
import app.user.model.User;
import app.web.dto.*;
import lombok.experimental.UtilityClass;


@UtilityClass
public class DtoMapper {
    public static UserEditRequest mapUserToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
    // Static method to map FriendshipInvitation to FriendshipInvitationResponse
    public static FriendshipInvitationResponse mapToFriendship(FriendshipInvitation invitation) {
        return FriendshipInvitationResponse.builder()
                .id(invitation.getId())
                .invitingUserId(invitation.getInvitingUser().getId())
                .invitingUsername(invitation.getInvitingUser().getUsername())
                .acceptingUserId(invitation.getAcceptingUser().getId())
                .acceptingUsername(invitation.getAcceptingUser().getUsername())
                .accepted(invitation.isAccepted())
                .build();
    }
    }
