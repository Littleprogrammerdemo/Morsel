package app.web.controller;

import app.bookmark.service.BookmarkService;
import app.post.model.Post;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/bookmarks")
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
    public ModelAndView addBookmark(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @PathVariable UUID postId) {

        User user = userService.getByUserId(authenticationMetadata.getUserId());

        bookmarkService.addBookmark(user.getId(), postId);

        ModelAndView modelAndView = new ModelAndView("bookmarks");
        modelAndView.addObject("message", "Recipe bookmarked!");

        List<Post> userBookmarks = bookmarkService.getBookmarksForUser(user.getId());
        modelAndView.addObject("bookmarks", userBookmarks);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @DeleteMapping("/remove/{postId}")
    public ModelAndView removeBookmark(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @PathVariable UUID postId) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());

        bookmarkService.removeBookmark(user.getId(), postId);

        ModelAndView modelAndView = new ModelAndView("bookmarks");
        modelAndView.addObject("message", "Bookmark removed!");

        // Fetch updated list of bookmarks for the user
        List<Post> userBookmarks = bookmarkService.getBookmarksForUser(user.getId());
        modelAndView.addObject("bookmarks", userBookmarks);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
