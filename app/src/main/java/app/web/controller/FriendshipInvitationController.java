package app.web.controller;

import app.follow.service.FriendshipInvitationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.FriendshipInvitationResponse;
import app.web.dto.UserResponse;
import app.web.dto.mapper.DtoMapper;  // Import DtoMapper
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
        List<FriendshipInvitationResponse> invitations = friendshipInvitationService
                .findPendingInvitations()
                .stream()
                .map(DtoMapper::mapToFriendship)  // Use DtoMapper to map FriendshipInvitation to FriendshipInvitationResponse
                .collect(Collectors.toList());

        model.addAttribute("invitations", invitations);
        return "friendship-pending";
    }

    // Show friends list
    @GetMapping("/friends")
    public String showFriends(@AuthenticationPrincipal AuthenticationMetadata authUser, Model model) {
        List<FriendshipInvitationResponse> friends = friendshipInvitationService
                .findAcceptedFriends()
                .stream()
                .map(DtoMapper::mapToFriendship)  // Use DtoMapper to map FriendshipInvitation to FriendshipInvitationResponse
                .collect(Collectors.toList());

        List<UserResponse> friendDetails = friends.stream().map(friend -> {
            User friendUser = userService.getByUserId(friend.getAcceptingUserId().equals(authUser.getUserId())
                    ? friend.getInvitingUserId()
                    : friend.getAcceptingUserId());

            return new UserResponse(friendUser.getId(), friendUser.getUsername(), friendUser.getFirstName(),
                    friendUser.getLastName(), friendUser.getProfilePicture());
        }).collect(Collectors.toList());

        model.addAttribute("friendDetails", friendDetails);
        return "friendship-list";
    }

    // Send a friendship invitation
    @PostMapping("/invite")
    public String sendFriendshipInvitation(@AuthenticationPrincipal AuthenticationMetadata authUser,
            @RequestParam("acceptingUserId") UUID acceptingUserId) {
        friendshipInvitationService.createFriendshipInvitation(acceptingUserId);
        return "redirect:/friendships/pending";
    }

    // Accept a friendship invitation
    @PostMapping("/accept/{friendshipId}")
    public String acceptFriendship(@AuthenticationPrincipal AuthenticationMetadata authUser,
            @PathVariable UUID friendshipId) {
        friendshipInvitationService.acceptFriendshipInvitation(friendshipId);
        return "redirect:/friendships/friends";
    }

    // Decline a friendship invitation
    @PostMapping("/decline/{friendshipId}")
    public String declineFriendship(
            @AuthenticationPrincipal AuthenticationMetadata authUser,
            @PathVariable UUID friendshipId) {
        friendshipInvitationService.declineFriendshipInvitation(friendshipId);
        return "redirect:/friendships/pending";
    }

    // Remove a friend
    @PostMapping("/remove/{friendshipId}")
    public String removeFriend(
            @AuthenticationPrincipal AuthenticationMetadata authUser,
            @PathVariable UUID friendshipId) {
        friendshipInvitationService.removeFriendship(friendshipId);
        return "redirect:/friendships/friends";
    }
}
