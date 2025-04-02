package app.unit_tests.bookmark;

import app.bookmark.service.BookmarkService;
import app.post.model.Post;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.BookmarkController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkControllerUnitTest {

    @Mock
    private BookmarkService bookmarkService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookmarkController bookmarkController;

    @Test
    void givenAuthenticatedUser_whenGetBookmarkNotifications_thenReturnModelAndView() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authMetadata = mock(AuthenticationMetadata.class);
        when(authMetadata.getUserId()).thenReturn(userId);

        User user = new User();
        user.setId(userId);
        List<Post> bookmarks = List.of(new Post(), new Post());

        when(userService.getByUserId(userId)).thenReturn(user);
        when(bookmarkService.getBookmarksForUser(userId)).thenReturn(bookmarks);

        // When
        ModelAndView modelAndView = bookmarkController.getBookmarkNotifications(authMetadata);

        // Then
        assertEquals("bookmarks", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(bookmarks, modelAndView.getModel().get("bookmarks"));
    }

    @Test
    void givenAuthenticatedUserAndPost_whenAddBookmark_thenReturnUpdatedModelAndView() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        AuthenticationMetadata authMetadata = mock(AuthenticationMetadata.class);
        when(authMetadata.getUserId()).thenReturn(userId);

        User user = new User();
        user.setId(userId);
        List<Post> bookmarks = List.of(new Post(), new Post());

        when(userService.getByUserId(userId)).thenReturn(user);
        doNothing().when(bookmarkService).addBookmark(userId, postId);
        when(bookmarkService.getBookmarksForUser(userId)).thenReturn(bookmarks);

        // When
        ModelAndView modelAndView = bookmarkController.addBookmark(authMetadata, postId);

        // Then
        assertEquals("bookmarks", modelAndView.getViewName());
        assertEquals("Post bookmarked!", modelAndView.getModel().get("message"));
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(bookmarks, modelAndView.getModel().get("bookmarks"));

        verify(bookmarkService, times(1)).addBookmark(userId, postId);
    }

    @Test
    void givenAuthenticatedUserAndPost_whenRemoveBookmark_thenReturnUpdatedModelAndView() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        AuthenticationMetadata authMetadata = mock(AuthenticationMetadata.class);
        when(authMetadata.getUserId()).thenReturn(userId);

        User user = new User();
        user.setId(userId);
        List<Post> bookmarks = List.of();

        when(userService.getByUserId(userId)).thenReturn(user);
        doNothing().when(bookmarkService).removeBookmark(userId, postId);
        when(bookmarkService.getBookmarksForUser(userId)).thenReturn(bookmarks);

        // When
        ModelAndView modelAndView = bookmarkController.removeBookmark(authMetadata, postId);

        // Then
        assertEquals("bookmarks", modelAndView.getViewName());
        assertEquals("Bookmark removed!", modelAndView.getModel().get("message"));
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(bookmarks, modelAndView.getModel().get("bookmarks"));

        verify(bookmarkService, times(1)).removeBookmark(userId, postId);
    }
}
