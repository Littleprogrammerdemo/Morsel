package app.comment.repository;

import app.comment.model.Comment;
import app.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPost_Id(UUID postId);

    List<Comment> findByCreatedOnAfter(LocalDateTime last15Minutes);
}
