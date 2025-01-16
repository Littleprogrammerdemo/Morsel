package app.rating.repository;


import app.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    // Find all ratings for a particular post
    List<Rating> findByPostId(UUID postId);

    // Find a specific rating by a user for a specific post
    Rating findByPostIdAndUserId(UUID postId, UUID userId);
}
