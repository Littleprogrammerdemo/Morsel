package app.web.controller;

import app.category.model.Category;
import app.category.model.CategoryType;
import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import app.web.dto.PostCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ModelAndView viewPost(@PathVariable UUID id) {
        log.debug("Viewing post with id: {}", id);
        ModelAndView modelAndView = new ModelAndView("post/view");
        modelAndView.addObject("posts", postService.getPostById(id));
        modelAndView.addObject("posts", postService.getCommentsForPost(id));  // Add comments
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView createPostForm() {
        log.debug("Creating new post");
        ModelAndView modelAndView = new ModelAndView("post/create");
        modelAndView.addObject("createRecipe", new PostCommand());  // Prepare form object
        return modelAndView;
    }

    @PostMapping("/update")
    public ModelAndView createOrUpdatePost(@RequestParam String title, @RequestParam String content, @RequestParam CategoryType category,@RequestParam("image") MultipartFile imageFile) {
        // Pass the title and content to the service for post creation
        postService.createPost(getCurrentUser(), title, content, category,imageFile);

        return new ModelAndView("redirect:/home");  // Redirect to home after successful operation
    }

    @GetMapping("/search")
    public ModelAndView searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        ModelAndView modelAndView = new ModelAndView("post/searchResults");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/filter")
    public ModelAndView filterByCategory(@RequestParam Category category) {
        List<Post> posts = postService.filterByCategory(category);
        ModelAndView modelAndView = new ModelAndView("post/filterResults");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @PostMapping("/{postId}/like")
    public ModelAndView likePost(@PathVariable UUID postId) {
        postService.likePost(postId, getCurrentUser());
        return new ModelAndView("redirect:/post/" + postId);  // Redirect back to the post
    }

    @PostMapping("/{postId}/rate")
    public ModelAndView ratePost(@PathVariable UUID postId, @RequestParam double rating) {
        postService.ratePost(postId, rating, getCurrentUser());
        return new ModelAndView("redirect:/post/" + postId);  // Redirect back to the post
    }

    @PostMapping("/{postId}/comment")
    public ModelAndView addComment(@PathVariable UUID postId, @RequestParam String content) {
        postService.addComment(postId, getCurrentUser(), content);
        return new ModelAndView("redirect:/post/" + postId);  // Redirect to the post page after the comment is added
    }

    @GetMapping("/{commentId}/delete")
    public ModelAndView deleteComment(@PathVariable UUID commentId) {
        postService.deleteCommentFromPost(commentId);
        return new ModelAndView("redirect:/home");  // Redirect to home after deletion
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return (User) authentication.getPrincipal();  // Assumes the principal is of type User
    }

    @PostMapping("/add-to-category/{categoryId}")
    public ModelAndView addRecipeToCategory(@PathVariable UUID categoryId, @RequestBody Post recipe) {
        Post updatedPost = postService.addRecipeToCategory(categoryId, recipe);
        ModelAndView modelAndView = new ModelAndView("redirect:/post/" + updatedPost.getId());  // Redirect to the post page after adding
        return modelAndView;
    }

    @GetMapping("/{postId}/delete")
    public ModelAndView deletePost(@PathVariable UUID id) {
        log.debug("Deleting post with id: {}", id);
        postService.deletePost(id, getCurrentUser());
        return new ModelAndView("redirect:/home");  // Redirect to home after deletion
    }
}
