package app.web.controller;

import app.post.model.Post;
import app.user.model.User;
import app.follow.service.FollowService;
import app.user.service.UserService;
import app.web.dto.FollowRequest;
import app.post.service.PostService;  // Assuming PostService is available
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/followers")
public class FollowController {

    private final UserService userService;
    private final FollowService followService;
    private final PostService postService;  // Assuming PostService is available

    @Autowired
    public FollowController(UserService userService, FollowService followService, PostService postService) {
        this.userService = userService;
        this.followService = followService;
        this.postService = postService;
    }

    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return "error"; // Handle user not found
        }

        // Retrieve the current user and check if they are following the target user
        boolean isFollowing = userService.findByUsername(userDetails.getUsername())
                .map(currentUser -> followService.getFollowing(currentUser).stream()
                        .anyMatch(follow -> follow.getFollowing().equals(user.get())))
                .orElse(false);

        // Retrieve the user's posts (you might have a PostService to fetch posts)
        List<Post> posts = postService.getPostsByUser(user.get());

        // Add user details, follow status, posts, and bio to the model
        model.addAttribute("user", user.get());
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("posts", posts);
        model.addAttribute("bio", user.get().getBio());  // Assuming bio is a field in User

        return "profile";  // Your profile template
    }

    @PostMapping("/{username}/follow")
    public String followUser(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userToFollow = userService.findByUsername(username);
        Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());

        if (userToFollow.isPresent() && currentUser.isPresent()) {
            FollowRequest followRequest = FollowRequest.builder()
                    .followerId(currentUser.get().getId())
                    .followedId(userToFollow.get().getId())
                    .followers(List.of(currentUser.get()))
                    .followedUsers(List.of(userToFollow.get()))
                    .build();

            followService.followUser(followRequest);
        }

        return "redirect:/users/" + username;
    }

    @PostMapping("/{username}/unfollow")
    public String unfollowUser(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userToUnfollow = userService.findByUsername(username);
        Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());

        if (userToUnfollow.isPresent() && currentUser.isPresent()) {
            FollowRequest followRequest = FollowRequest.builder()
                    .followerId(currentUser.get().getId())
                    .followedId(userToUnfollow.get().getId())
                    .followers(List.of(currentUser.get()))
                    .followedUsers(List.of(userToUnfollow.get()))
                    .build();

            followService.unfollowUser(followRequest);
        }

        return "redirect:/users/" + username;
    }
}
