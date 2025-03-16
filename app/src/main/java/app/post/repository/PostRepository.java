package app.post.repository;

import app.category.model.Category;
import app.post.model.Post;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchPosts(@Param("keyword") String keyword);

    // Filter by category
    List<Post> findByCategory(Category category);
    // Намира рецепти с нула лайкове и създадени преди повече от 1 година
    List<Post> findByLikesAndCreatedOnBefore(int likes, LocalDateTime createdOn);
    List<Post> findByUser(User user);
}