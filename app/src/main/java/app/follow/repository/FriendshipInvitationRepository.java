package app.follow.repository;

import app.follow.model.FriendshipInvitation;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface FriendshipInvitationRepository extends JpaRepository<FriendshipInvitation, UUID> {
    List<FriendshipInvitation> findByAcceptingUserAndAcceptedFalse(User acceptingUser);

    List<FriendshipInvitation> findByInvitingUserOrAcceptingUserAndAcceptedTrue(User user1, User user2);

    boolean existsByInvitingUserAndAcceptingUser(User invitingUser, User acceptingUser);
}
