package app.follow.service;

import app.follow.model.Follow;
import app.user.model.User;
import app.follow.repository.FollowRepository;
import app.web.dto.FollowRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowService {
    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void followUser(FollowRequest followRequest) {
        // Get followers and following users using their IDs from the FollowRequest
        Optional<User> follower = followRequest.getFollowers().stream()
                .filter(user -> user.getId().equals(followRequest.getFollowerId()))
                .findFirst();
        Optional<User> following = followRequest.getFollowedUsers().stream()
                .filter(user -> user.getId().equals(followRequest.getFollowedId()))
                .findFirst();

        if (follower.isPresent() && following.isPresent()) {
            User followerUser = follower.get();
            User followingUser = following.get();

            if (!followRepository.existsByFollowerAndFollowing(followerUser, followingUser)) {
                Follow follow = new Follow();
                follow.setFollower(followerUser);
                follow.setFollowing(followingUser);
                followRepository.save(follow);
            }
        }
    }

    public void unfollowUser(FollowRequest followRequest) {
        // Get followers and following users using their IDs from the FollowRequest
        Optional<User> follower = followRequest.getFollowers().stream()
                .filter(user -> user.getId().equals(followRequest.getFollowerId()))
                .findFirst();
        Optional<User> following = followRequest.getFollowedUsers().stream()
                .filter(user -> user.getId().equals(followRequest.getFollowedId()))
                .findFirst();

        if (follower.isPresent() && following.isPresent()) {
            followRepository.deleteByFollowerAndFollowing(follower.get(), following.get());
        }
    }

    public List<Follow> getFollowers(User user) {
        return followRepository.findByFollowing(user);
    }

    public List<Follow> getFollowing(User user) {
        return followRepository.findByFollower(user);
    }
}