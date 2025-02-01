package app.bookmark.repository;

import app.bookmark.model.Bookmark;
import app.post.model.Post;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
}
