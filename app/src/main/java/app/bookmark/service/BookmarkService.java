package app.bookmark.service;

import app.bookmark.model.Bookmark;
import app.post.model.Post;
import app.post.service.PostService;
import app.bookmark.repository.BookmarkRepository;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final PostService postService;

    public BookmarkService(BookmarkRepository bookmarkRepository, UserService userService, PostService postService) {
        this.bookmarkRepository = bookmarkRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public void addBookmark(UUID userId, UUID postId) {
        User user = userService.getByUserId(userId);
        Post post = postService.getPostById(postId);

        if (bookmarkRepository.findByUserAndPost(user, post).isEmpty()) {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setPost(post);
            bookmarkRepository.save(bookmark);
        }
    }

    public void removeBookmark(UUID userId, UUID postId) {
        User user = userService.getByUserId(userId);
        Post post = postService.getPostById(postId);

        bookmarkRepository.deleteByUserAndPost(user, post);
    }

    public List<Post> getBookmarksForUser(UUID userId) {
        User user = userService.getByUserId(userId);
        return bookmarkRepository.findByUser(user).stream().map(Bookmark::getPost).collect(Collectors.toList());
    }
}

