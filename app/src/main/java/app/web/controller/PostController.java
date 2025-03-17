package app.web.controller;

import app.category.model.Category;
import app.category.model.CategoryType;
import app.post.model.Post;
import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateNewPost;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
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
        modelAndView.addObject("post", posts);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewPost(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("post", postService.getPostById(id));
        modelAndView.addObject("comments", postService.getCommentsForPost(id));  // Add comments
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView createPostForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId()); // Get the user from AuthenticationMetadata
        ModelAndView modelAndView = new ModelAndView("createRecipe");
        modelAndView.addObject("createRecipe", new CreateNewPost());  // Prepare form object
        modelAndView.addObject("user", user);  // Add user to the model
        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView createOrUpdatePost(@Valid CreateNewPost createNewPost,
                                           @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.createPost(user, createNewPost);
        return new ModelAndView("redirect:/home");  // Redirect to home after successful operation
    }

    @PostMapping("/{id}/like")
    public ModelAndView likePost(@PathVariable UUID id) {
        postService.likePost(id);
        return new ModelAndView("redirect:/posts/" + id);
    }

    @PostMapping("/{id}/share")
    public ModelAndView sharePost(@PathVariable UUID id) {
        postService.sharePost(id);
        return new ModelAndView("redirect:/posts/" + id);
    }
    @PostMapping("/{id}/rate")
    public ModelAndView ratePost(@PathVariable UUID id, @RequestParam int rating,
                                 @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());

        postService.ratePost(id, user, rating);

        return new ModelAndView("redirect:/posts/" + id);
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