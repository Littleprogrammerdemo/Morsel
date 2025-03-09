package app.web.controller;

import app.category.model.Category;
import app.category.model.CategoryType;
import app.post.model.Post;
import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.PostCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getAllPosts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        List<Post> posts = postService.getAllPosts();
        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("user", user);
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewPost(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView("post/view");
        modelAndView.addObject("post", postService.getPostById(id));
        modelAndView.addObject("comments", postService.getCommentsForPost(id));  // Add comments
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView createPostForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId()); // Get the user from AuthenticationMetadata
        ModelAndView modelAndView = new ModelAndView("createRecipe");
        modelAndView.addObject("createRecipe", new PostCommand());  // Prepare form object
        modelAndView.addObject("user", user);  // Add user to the model
        return modelAndView;
    }

    @PostMapping("/update")
    public ModelAndView createOrUpdatePost(@RequestParam String title, @RequestParam String content,
                                           @RequestParam CategoryType category, @RequestParam("image") MultipartFile imageFile,
                                           @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.createPost(user, title, content, category, imageFile);
        return new ModelAndView("redirect:/home");  // Redirect to home after successful operation
    }

    @GetMapping("/search")
    public ModelAndView searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/filter")
    public ModelAndView filterByCategory(@RequestParam Category category) {
        List<Post> posts = postService.filterByCategory(category);
        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @PostMapping("/{id}/like")
    public ModelAndView likePost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.likePost(id, user);
        return new ModelAndView("redirect:/posts/" + id);  // Redirect back to the post
    }

    @PostMapping("/{id}/rate")
    public ModelAndView ratePost(@PathVariable UUID id, @RequestParam double rating,
                                 @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.ratePost(id, rating, user);
        return new ModelAndView("redirect:/posts/" + id);  // Redirect back to the post
    }

    @PostMapping("/{id}/comment")
    public ModelAndView addComment(@PathVariable UUID id, @RequestParam String content,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.addComment(id, user, content);
        return new ModelAndView("redirect:/posts/" + id);  // Redirect to the post page after the comment is added
    }

    @DeleteMapping("/{commentId}")
    public ModelAndView deleteComment(@PathVariable UUID commentId) {
        postService.deleteCommentFromPost(commentId);
        return new ModelAndView("redirect:/home");  // Redirect to home after deletion
    }

    @DeleteMapping("/{id}")
    public ModelAndView deletePost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.deletePost(id, user);
        return new ModelAndView("redirect:/home");  // Redirect to home after deletion
    }

}
