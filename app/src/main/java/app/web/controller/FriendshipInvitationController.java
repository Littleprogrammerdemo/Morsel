package app.web.controller;

import app.follow.service.FriendshipInvitationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.FriendshipInvitationResponse;
import app.web.dto.UserResponse;
import app.web.dto.mapper.DtoMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/friendships")
public class FriendshipInvitationController {

    private final FriendshipInvitationService friendshipInvitationService;
    private final UserService userService;

    public FriendshipInvitationController(FriendshipInvitationService friendshipInvitationService, UserService userService) {
        this.friendshipInvitationService = friendshipInvitationService;
        this.userService = userService;
    }

    // Show pending friendship invitations
    @GetMapping("/pending")
    public String showPendingInvitations(@AuthenticationPrincipal AuthenticationMetadata authUser, Model model) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        List<FriendshipInvitationResponse> invitations = friendshipInvitationService
                .findPendingInvitations(authenticatedUser)
                .stream()
                .map(DtoMapper::mapToFriendship)
                .collect(Collectors.toList());

        List<FriendshipInvitationResponse> invitationsWithDetails = invitations.stream()
                .map(invitation -> {
                    User invitingUser = userService.getByUserId(invitation.getInvitingUserId());
                    invitation.setInvitingUserResponse(new UserResponse(
                            invitingUser.getId(), invitingUser.getUsername(),
                            invitingUser.getFirstName(), invitingUser.getLastName(),
                            invitingUser.getProfilePicture()
                    ));
                    return invitation;
                })
                .collect(Collectors.toList());

        model.addAttribute("invitations", invitationsWithDetails);
        model.addAttribute("user", authenticatedUser);

        return "friendship-pending";
    }

    // Show list of accepted friends
    @GetMapping("/friends")
    public String showFriends(@AuthenticationPrincipal AuthenticationMetadata authUser, Model model) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        List<FriendshipInvitationResponse> friends = friendshipInvitationService
                .findAcceptedFriends(authenticatedUser)
                .stream()
                .map(DtoMapper::mapToFriendship)
                .collect(Collectors.toList());

        List<UserResponse> friendDetails = friends.stream().map(friend -> {
            User friendUser = userService.getByUserId(
                    friend.getAcceptingUserId().equals(authUser.getUserId())
                            ? friend.getInvitingUserId()
                            : friend.getAcceptingUserId()
            );
            return new UserResponse(friendUser.getId(), friendUser.getUsername(),
                    friendUser.getFirstName(), friendUser.getLastName(),
                    friendUser.getProfilePicture());
        }).collect(Collectors.toList());

        model.addAttribute("friendDetails", friendDetails);
        model.addAttribute("user", authenticatedUser);

        return "friendship-list";
    }

    // Show invite form
    @GetMapping("/invite")
    public String showInviteForm(@AuthenticationPrincipal AuthenticationMetadata authUser, Model model) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        List<UserResponse> users = userService.getAllUsers().stream()
                .filter(user -> !user.getId().equals(authenticatedUser.getId()))
                .map(user -> new UserResponse(user.getId(), user.getUsername(),
                        user.getFirstName(), user.getLastName(),
                        user.getProfilePicture()))
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        return "friendship-invite";
    }

    // Send a friendship invitation
    @PostMapping("/invite")
    public String sendFriendshipInvitation(@AuthenticationPrincipal AuthenticationMetadata authUser,
                                           @RequestParam("acceptingUserId") UUID acceptingUserId) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        if (acceptingUserId != null) {
            friendshipInvitationService.createFriendshipInvitation(authenticatedUser, acceptingUserId);
        }

        return "redirect:/friendships/pending";
    }

    // Accept a friendship invitation
    @PostMapping("/accept/{friendshipId}")
    public String acceptFriendship(@AuthenticationPrincipal AuthenticationMetadata authUser,
                                   @PathVariable UUID friendshipId) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        friendshipInvitationService.acceptFriendshipInvitation(authenticatedUser, friendshipId);

        return "redirect:/friendships/friends";
    }

    // Decline a friendship invitation
    @PostMapping("/decline/{friendshipId}")
    public String declineFriendship(@AuthenticationPrincipal AuthenticationMetadata authUser,
                                    @PathVariable UUID friendshipId) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        friendshipInvitationService.declineFriendshipInvitation(authenticatedUser, friendshipId);

        return "redirect:/friendships/pending";
    }

    // Remove a friend
    @PostMapping("/remove/{friendshipId}")
    public String removeFriend(@AuthenticationPrincipal AuthenticationMetadata authUser,
                               @PathVariable UUID friendshipId) {
        User authenticatedUser = userService.getByUserId(authUser.getUserId());

        friendshipInvitationService.removeFriendship(authenticatedUser, friendshipId);

        return "redirect:/friendships/friends";
    }
}
