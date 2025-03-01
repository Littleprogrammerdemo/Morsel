package app.web.controller;

import app.bookmark.service.BookmarkService;
import app.post.model.Post;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;

    public BookmarkController(BookmarkService bookmarkService, UserService userService) {
        this.bookmarkService = bookmarkService;
        this.userService = userService;
    }
    @GetMapping()
    public ModelAndView getBookmarkNotifications(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getByUserId(authenticationMetadata.getUserId());

        List<Post> userBookmarks = bookmarkService.getBookmarksForUser(user.getId());

        // Create the ModelAndView object
        ModelAndView modelAndView = new ModelAndView("bookmarks");
        modelAndView.addObject("user", user);
        modelAndView.addObject("bookmarks", userBookmarks);

        return modelAndView;
    }
    @PostMapping("/add/{postId}")
    public ResponseEntity<String> addBookmark(@RequestParam UUID userId, @PathVariable UUID postId) {
        bookmarkService.addBookmark(userId, postId);
        return ResponseEntity.ok("Recipe bookmarked!");
    }

    @DeleteMapping("/remove/{postId}")
    public ResponseEntity<String> removeBookmark(@RequestParam UUID userId, @PathVariable UUID postId) {
        bookmarkService.removeBookmark(userId, postId);
        return ResponseEntity.ok("Bookmark removed!");
    }
}
