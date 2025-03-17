package app.post.repository;

import app.post.model.Post;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    // Намира рецепти с нула лайкове и създадени преди повече от 1 година
    List<Post> findByLikesAndCreatedOnBefore(int likes, LocalDateTime createdOn);
    List<Post> findByUser(User user);

    List<Post> findTop10ByOrderByLikesDescViewsDesc();
}