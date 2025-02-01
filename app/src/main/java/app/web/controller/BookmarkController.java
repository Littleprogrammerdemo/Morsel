package app.web.controller;

import app.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
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
