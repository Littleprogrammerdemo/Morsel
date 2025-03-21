package app.follow.service;

import app.follow.model.FriendshipInvitation;
import app.follow.repository.FriendshipInvitationRepository;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service
public class FriendshipInvitationService {

    private final FriendshipInvitationRepository friendshipInvitationRepository;
    private final UserRepository userRepository; // Fetch User entities

    @Autowired
    public FriendshipInvitationService(FriendshipInvitationRepository friendshipInvitationRepository, UserRepository userRepository) {
        this.friendshipInvitationRepository = friendshipInvitationRepository;
        this.userRepository = userRepository;
    }

    // Get the currently authenticated user
    private User getAuthenticatedUser() {
        AuthenticationMetadata authUser = (AuthenticationMetadata) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    public FriendshipInvitation createFriendshipInvitation(UUID acceptingUserId) {
        // Get the current authenticated user
        User invitingUser = getAuthenticatedUser(); // Get the currently authenticated user

        // Ensure the authenticated user is not null
        if (invitingUser == null) {
            throw new IllegalStateException("No authenticated user found.");
        }

        // Get the accepting user by ID
        User acceptingUser = userRepository.findById(acceptingUserId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Prevent self-invitation
        if (invitingUser.getId().equals(acceptingUser.getId())) {
            throw new IllegalStateException("You cannot send a friend request to yourself.");
        }

        // Prevent duplicate requests (check both directions)
        boolean alreadySent = friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(invitingUser, acceptingUser);
        boolean alreadyReceived = friendshipInvitationRepository.existsByInvitingUserAndAcceptingUser(acceptingUser, invitingUser);

        if (alreadySent || alreadyReceived) {
            throw new IllegalStateException("Friendship request already exists.");
        }

        // Create a new friendship invitation
        FriendshipInvitation invitation = new FriendshipInvitation(invitingUser, acceptingUser);

        // Save the invitation and return it
        return friendshipInvitationRepository.save(invitation);
    }


    // Get all pending invitations for the logged-in user
    public List<FriendshipInvitation> findPendingInvitations() {
        User acceptingUser = getAuthenticatedUser();
        return friendshipInvitationRepository.findByAcceptingUserAndAcceptedFalse(acceptingUser);
    }

    // Get all accepted friendships for the logged-in user
    public List<FriendshipInvitation> findAcceptedFriends() {
        User user = getAuthenticatedUser();
        return friendshipInvitationRepository.findByInvitingUserOrAcceptingUserAndAcceptedTrue(user, user);
    }

    // Accept a friendship invitation
    @Transactional
    public void acceptFriendshipInvitation(UUID friendshipId) {
        FriendshipInvitation invitation = friendshipInvitationRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalStateException("Friendship invitation not found"));

        User authenticatedUser = getAuthenticatedUser();
        if (!invitation.getAcceptingUser().equals(authenticatedUser)) {
            throw new IllegalStateException("You are not authorized to accept this friendship request.");
        }

        if (invitation.isAccepted()) {
            throw new IllegalStateException("Friendship is already accepted.");
        }

        invitation.setAccepted(true);
        friendshipInvitationRepository.save(invitation);
    }

    // Decline a friendship invitation
    public void declineFriendshipInvitation(UUID friendshipId) {
        FriendshipInvitation invitation = friendshipInvitationRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalStateException("Friendship invitation not found"));

        User authenticatedUser = getAuthenticatedUser();
        if (!invitation.getAcceptingUser().equals(authenticatedUser)) {
            throw new IllegalStateException("You are not authorized to decline this friendship request.");
        }

        friendshipInvitationRepository.deleteById(friendshipId);
    }

    // Remove a friendship
    @Transactional
    public void removeFriendship(UUID friendshipId) {
        FriendshipInvitation invitation = friendshipInvitationRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalStateException("Friendship not found"));

        User authenticatedUser = getAuthenticatedUser();
        if (!invitation.getInvitingUser().equals(authenticatedUser) &&
                !invitation.getAcceptingUser().equals(authenticatedUser)) {
            throw new IllegalStateException("You are not authorized to remove this friendship.");
        }

        friendshipInvitationRepository.deleteById(friendshipId);
    }
}
