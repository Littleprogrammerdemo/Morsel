package app.follow.service;

import app.follow.model.Follow;
import app.user.model.User;
import app.follow.repository.FollowRepository;
import app.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    public FollowService(FollowRepository followRepository,UserService userService) {
        this.followRepository = followRepository;
        this.userService = userService;
    }

    public void followUser(UUID followerId, UUID followedId) {
        User follower = userService.getByUserId(followerId);
        User followed = userService.getByUserId(followedId);

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isEmpty()) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);
        }
    }

    public void unfollowUser(UUID followerId, UUID followedId) {
        User follower = userService.getByUserId(followerId);
        User followed =  userService.getByUserId(followedId);

        followRepository.deleteByFollowerAndFollowed(follower, followed);
    }

    public List<User> getFollowedUsers(UUID followerId) {
        User follower = userService.getByUserId(followerId);

        return followRepository.findByFollower(follower)
                .stream()
                .map(Follow::getFollowed)
                .collect(Collectors.toList());
    }
}
