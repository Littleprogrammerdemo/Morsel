package app.unit_tests.bookmark;

import app.bookmark.model.Bookmark;
import app.bookmark.repository.BookmarkRepository;
import app.bookmark.service.BookmarkService;
import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceUnitTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    void givenValidUserAndPost_whenAddBookmark_thenSaveBookmark() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User user = new User();
        Post post = new Post();

        when(userService.getByUserId(userId)).thenReturn(user);
        when(postService.getPostById(postId)).thenReturn(post);
        when(bookmarkRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        // When
        bookmarkService.addBookmark(userId, postId);

        // Then
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    void givenExistingBookmark_whenAddBookmark_thenDoNothing() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User user = new User();
        Post post = new Post();

        when(userService.getByUserId(userId)).thenReturn(user);
        when(postService.getPostById(postId)).thenReturn(post);
        when(bookmarkRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(new Bookmark()));

        // When
        bookmarkService.addBookmark(userId, postId);

        // Then
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    void givenValidUserAndPost_whenRemoveBookmark_thenDeleteBookmark() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User user = new User();
        Post post = new Post();

        when(userService.getByUserId(userId)).thenReturn(user);
        when(postService.getPostById(postId)).thenReturn(post);

        // When
        bookmarkService.removeBookmark(userId, postId);

        // Then
        verify(bookmarkRepository, times(1)).deleteByUserAndPost(user, post);
    }

    @Test
    void givenUserWithBookmarks_whenGetBookmarksForUser_thenReturnPosts() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        Post post1 = new Post();
        Post post2 = new Post();
        Bookmark bookmark1 = new Bookmark();
        bookmark1.setPost(post1);
        Bookmark bookmark2 = new Bookmark();
        bookmark2.setPost(post2);

        when(userService.getByUserId(userId)).thenReturn(user);
        when(bookmarkRepository.findByUser(user)).thenReturn(List.of(bookmark1, bookmark2));

        // When
        List<Post> bookmarkedPosts = bookmarkService.getBookmarksForUser(userId);

        // Then
        assertEquals(2, bookmarkedPosts.size());
        assertTrue(bookmarkedPosts.contains(post1));
        assertTrue(bookmarkedPosts.contains(post2));
    }
}
