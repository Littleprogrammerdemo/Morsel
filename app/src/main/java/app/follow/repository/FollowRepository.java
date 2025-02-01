package app.follow.repository;

import app.follow.model.Follow;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
    List<Follow> findByFollower(User follower);
    void deleteByFollowerAndFollowed(User follower, User followed);
}
