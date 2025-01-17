package app.web.controller;

import app.rating.service.RatingService;
import app.user.model.User;
import app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;  // Assuming you have a UserService to fetch the user

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @PostMapping("/post/{postId}/rate")
    public String ratePost(@PathVariable UUID postId, @RequestParam double rating) {
        log.debug("Rating post with id: {} with rating: {}", postId, rating);

        // Get the currently authenticated user from the Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = userService.getUserByUsername(authentication.getName());  // Assuming you have this method to get the user by username

        // Call the rating service with the current user and the rating
        ratingService.ratePost(postId, getCurrentUser(), currentUser, rating);

        return "redirect:/post/" + postId;  // Redirect to the post after rating
    }

    private User getCurrentUser() {
        // Replace this with actual logic to retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return (User) authentication.getPrincipal(); // Assumes the principal is of type User
    }
}
