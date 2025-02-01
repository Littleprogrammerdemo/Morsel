package app.web.controller;

import app.category.model.CategoryType;
import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import app.web.dto.PostCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable UUID id, Model model) {
        log.debug("Viewing post with id: {}", id);
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("comments", postService.getCommentsForPost(id));  // Добавяне на коментари
        return "post/view";
    }

    @GetMapping("/post/new")
    public String createPostForm(Model model) {
        log.debug("Creating new post");
        model.addAttribute("post", new PostCommand());  // Prepare form object
        return "post/create";
    }

    @PostMapping("/post")
    public String createOrUpdatePost(@RequestParam String title, @RequestParam String content, @RequestParam CategoryType category) {
        // Pass the title and content to the service for post creation
        postService.createPost(getCurrentUser(), title, content,category);

        return "redirect:/home";  // Redirect to home after successful operation
    }


    // Логика за лайкване на пост
    @PostMapping("/post/{postId}/like")
    public String likePost(@PathVariable UUID postId) {
        postService.likePost(postId, getCurrentUser());
        return "redirect:/post/" + postId;  // Redirect back to the post
    }

    // Логика за рейтинг на пост
    @PostMapping("/post/{postId}/rate")
    public String ratePost(@PathVariable UUID postId, @RequestParam double rating) {
        postService.ratePost(postId, rating, getCurrentUser());
        return "redirect:/post/" + postId;  // Redirect back to the post
    }

    // Добавяне на коментар
    @PostMapping("/post/{postId}/comment")
    public String addComment(@PathVariable UUID postId, @RequestParam String content) {
        postService.addComment(postId, getCurrentUser(), content);
        return "redirect:/post/" + postId;  // Redirect to the post page after the comment is added
    }

    // Изтриване на коментар
    @GetMapping("/comment/{commentId}/delete")
    public String deleteComment(@PathVariable UUID commentId) {
        postService.deleteCommentFromPost(commentId);
        return "redirect:/home";  // Redirect to home after deletion
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return (User) authentication.getPrincipal();  // Assumes the principal is of type User
    }
    @PostMapping("/add-to-category/{categoryId}")
    public Post addRecipeToCategory(@PathVariable UUID categoryId, @RequestBody Post recipe) {
        return postService.addRecipeToCategory(categoryId, recipe);
    }

    // Изтриване на пост
    @GetMapping("/post/{id}/delete")
    public String deletePost(@PathVariable UUID id, @PathVariable User user) {
        log.debug("Deleting post with id: {}", id);
        postService.deletePost(id,user);
        return "redirect:/home";  // Redirect to home after deletion
    }
}