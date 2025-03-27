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
        List<FriendshipInvitationResponse> invitations = friendshipInvitationService
                .findPendingInvitations()
                .stream()
                .map(DtoMapper::mapToFriendship)
                .collect(Collectors.toList());
        User user = userService.getByUserId(authUser.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("invitations", invitations);
        return "friendship-pending"; // Thymeleaf template
    }

    @GetMapping("/friends")
    public String showFriends(@AuthenticationPrincipal AuthenticationMetadata authUser, Model model) {
        // Get the list of accepted friends
        List<FriendshipInvitationResponse> friends = friendshipInvitationService
                .findAcceptedFriends()
                .stream()
                .map(DtoMapper::mapToFriendship)
                .collect(Collectors.toList());

        // Get details for each friend
        List<UserResponse> friendDetails = friends.stream().map(friend -> {
            User friendUser = userService.getByUserId(friend.getAcceptingUserId().equals(authUser.getUserId())
                    ? friend.getInvitingUserId()
                    : friend.getAcceptingUserId());

            return new UserResponse(friendUser.getId(), friendUser.getUsername(), friendUser.getFirstName(),
                    friendUser.getLastName(), friendUser.getProfilePicture());
        }).collect(Collectors.toList());

        // Get the current authenticated user
        User user = userService.getByUserId(authUser.getUserId());

        // Add attributes to the model
        model.addAttribute("friendDetails", friendDetails);
        model.addAttribute("user", user); // Pass the complete user object

        // Return the Thymeleaf template for displaying the friendship list
        return "friendship-list";
    }


    @GetMapping("/invite")
    public String showInviteForm(@AuthenticationPrincipal AuthenticationMetadata authUser, Model model) {
        // Get all users except the currently authenticated one
        List<UserResponse> users = userService.getAllUsers().stream()
                .filter(user -> !user.getId().equals(authUser.getUserId())) // Exclude self
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getFirstName(),
                        user.getLastName(), user.getProfilePicture()))
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        return "friendship-invite"; // Thymeleaf template
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
    public String declineFriendship(@AuthenticationPrincipal AuthenticationMetadata authUser,
                                    @PathVariable UUID friendshipId) {
        friendshipInvitationService.declineFriendshipInvitation(friendshipId);
        return "redirect:/friendships/pending";
    }

    // Remove a friend
    @PostMapping("/remove/{friendshipId}")
    public String removeFriend(@AuthenticationPrincipal AuthenticationMetadata authUser,
                               @PathVariable UUID friendshipId) {
        friendshipInvitationService.removeFriendship(friendshipId);
        return "redirect:/friendships/friends";
    }
}
