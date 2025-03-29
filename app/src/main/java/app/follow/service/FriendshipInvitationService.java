package app.follow.service;

import app.follow.model.FriendshipInvitation;
import app.follow.repository.FriendshipInvitationRepository;
import app.user.model.User;
import app.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FriendshipInvitationService {

    private final FriendshipInvitationRepository friendshipInvitationRepository;
    private final UserRepository userRepository; // Fetch User entities

    public FriendshipInvitationService(FriendshipInvitationRepository friendshipInvitationRepository, UserRepository userRepository) {
        this.friendshipInvitationRepository = friendshipInvitationRepository;
        this.userRepository = userRepository;
    }

    // Remove authentication logic, passing the authenticated user directly
    private User getAuthenticatedUser(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is not provided.");
        }
        return authenticatedUser;
    }

    @Transactional
    public FriendshipInvitation createFriendshipInvitation(User authenticatedUser, UUID acceptingUserId) {
        // Ensure the authenticated user is provided
        User invitingUser = getAuthenticatedUser(authenticatedUser);

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
    public List<FriendshipInvitation> findPendingInvitations(User authenticatedUser) {
        User acceptingUser = getAuthenticatedUser(authenticatedUser);
        return friendshipInvitationRepository.findByAcceptingUserAndAcceptedFalse(acceptingUser);
    }

    // Get all accepted friendships for the logged-in user
    public List<FriendshipInvitation> findAcceptedFriends(User authenticatedUser) {
        User user = getAuthenticatedUser(authenticatedUser);
        return friendshipInvitationRepository.findByInvitingUserOrAcceptingUserAndAcceptedTrue(user, user);
    }

    // Accept a friendship invitation
    @Transactional
    public void acceptFriendshipInvitation(User authenticatedUser, UUID friendshipId) {
        FriendshipInvitation invitation = friendshipInvitationRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalStateException("Friendship invitation not found"));

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
    public void declineFriendshipInvitation(User authenticatedUser, UUID friendshipId) {
        FriendshipInvitation invitation = friendshipInvitationRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalStateException("Friendship invitation not found"));

        if (!invitation.getAcceptingUser().equals(authenticatedUser)) {
            throw new IllegalStateException("You are not authorized to decline this friendship request.");
        }

        friendshipInvitationRepository.deleteById(friendshipId);
    }

    // Remove a friendship
    @Transactional
    public void removeFriendship(User authenticatedUser, UUID friendshipId) {
        FriendshipInvitation invitation = friendshipInvitationRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalStateException("Friendship not found"));

        if (!invitation.getInvitingUser().equals(authenticatedUser) &&
                !invitation.getAcceptingUser().equals(authenticatedUser)) {
            throw new IllegalStateException("You are not authorized to remove this friendship.");
        }

        friendshipInvitationRepository.deleteById(friendshipId);
    }
}
