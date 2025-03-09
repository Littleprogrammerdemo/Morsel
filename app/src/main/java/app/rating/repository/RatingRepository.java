package app.rating.repository;


import app.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    // Find all ratings for a particular post
    List<Rating> findByPostId(UUID postId);

    // Find a specific rating by a user for a specific post
    Rating findByPostIdAndOwner_Id(UUID postId, UUID ownerId);
}
