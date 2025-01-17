package app.rating.service;

import app.post.model.Post;
import app.post.service.PostService;
import app.rating.model.Rating;
import app.rating.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PostService postService;  // To fetch the post by its ID

    // Add or update a rating for a post
    public void ratePost(UUID postId, String user, double ratingValue) {
        if (ratingValue < 1.0 || ratingValue > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0 stars.");
        }

        Post post = postService.getPostById(postId);  // Get the post to be rated

        // Check if the user has already rated this post
        Rating existingRating = ratingRepository.findByPostIdAndUserId(postId, user.getId());

        if (existingRating != null) {
            // If the user has already rated, update the rating
            existingRating.setRating(ratingValue);
            existingRating.setCreatedOn(LocalDateTime.now());
            ratingRepository.save(existingRating);
        } else {
            // If the user hasn't rated, create a new rating
            Rating newRating = Rating.builder()
                    .post(post)
                    .owner(user)
                    .rating(ratingValue)
                    .createdOn(LocalDateTime.now())
                    .build();
            ratingRepository.save(newRating);
        }
    }

    // Get all ratings for a post
    public List<Rating> getRatingsForPost(UUID postId) {

        return ratingRepository.findByPostId(postId);
    }

    // Calculate the average rating of a post
    public double calculateAverageRating(UUID postId) {
        List<Rating> ratings = ratingRepository.findByPostId(postId);
        if (ratings.isEmpty()) {
            return 0.0;  // Return 0 if no ratings exist
        }
        double total = ratings.stream().mapToDouble(Rating::getRating).sum();
        return total / ratings.size();  // Calculate average rating
    }

}
