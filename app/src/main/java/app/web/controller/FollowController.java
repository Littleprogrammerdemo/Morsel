package app.web.controller;

import app.user.model.User;
import app.follow.service.FollowService;
import app.web.dto.FollowRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/followers")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping()
    public ResponseEntity<FollowRequest> getFollowersAndFollowing(@PathVariable UUID userId) {
        // Get both followers and followed users
        List<User> followers = followService.getFollowers(userId);
        List<User> followedUsers = followService.getFollowedUsers(userId);

        // Create and populate the response DTO
        FollowRequest response = new FollowRequest();
        response.setFollowers(followers);
        response.setFollowedUsers(followedUsers);

        // Return the response as a DTO
        return ResponseEntity.ok(response);
    }

    @PostMapping("/follow/{followedId}")
    public ResponseEntity<String> followUser(@RequestParam UUID followerId, @PathVariable UUID followedId) {
        followService.followUser(followerId, followedId);
        return ResponseEntity.ok("You are now following this user!");
    }

    @DeleteMapping("/unfollow/{followedId}")
    public ResponseEntity<String> unfollowUser(@RequestParam UUID followerId, @PathVariable UUID followedId) {
        followService.unfollowUser(followerId, followedId);
        return ResponseEntity.ok("You have unfollowed this user!");
    }

    @GetMapping("/following/{followerId}")
    public ResponseEntity<List<User>> getFollowedUsers(@PathVariable UUID followerId) {
        return ResponseEntity.ok(followService.getFollowedUsers(followerId));
    }
}
