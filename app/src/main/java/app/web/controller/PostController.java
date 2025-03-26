package app.web.controller;

import app.comment.model.Comment;
import app.post.model.Post;
import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateNewPost;
import app.web.dto.UpdatePostRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
        User user = userService.getByUserId(authenticationMetadata.getUserId());  // Get the current user
        List<Post> posts = postService.getPostsByUser(user);  // Get posts by this user
        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("user", user);
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }


    @GetMapping("/{id}")
    public ModelAndView viewPost(@PathVariable UUID id) {
        Post post = postService.getPostById(id);
        List<Comment> comments = postService.getCommentsForPost(id); // Added comments

        if (post == null) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        if (comments == null) {
            comments = List.of(); // Empty list if no comments exist
        }

        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("post", post);
        modelAndView.addObject("comments", comments);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView createPostForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId()); // Get the user from AuthenticationMetadata
        ModelAndView modelAndView = new ModelAndView("createPost");
        modelAndView.addObject("createNewPost", new CreateNewPost());  // Prepare form object
        modelAndView.addObject("user", user);  // Add user to the model
        return modelAndView;
    }

    @PostMapping()
    public ModelAndView createPost(@Valid CreateNewPost createNewPost,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());

        // If there are errors in the binding (e.g., validation errors), return the same form view
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("createPost");
            modelAndView.addObject("createNewPost", createNewPost);  // Populate the form with the invalid data
            modelAndView.addObject("user", user);  // Add user to the model
            return modelAndView;
        }

        postService.createPost(user, createNewPost);

        // Redirect to the posts page after successful creation
        return new ModelAndView("redirect:/posts");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editPostForm(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);
        // Check if the current user is the owner of the post
        if (!post.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to edit this post.");
        }

        ModelAndView modelAndView = new ModelAndView("editPost");
        modelAndView.addObject("updatePost", new UpdatePostRequest(post.getTitle(), post.getContent(), post.getCategoryType().name(), null));
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/{id}/update")
    public ModelAndView updatePost(@PathVariable UUID id, @Valid UpdatePostRequest updatePost, BindingResult bindingResult,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("editPost");
            modelAndView.addObject("updatePost", updatePost);
            return modelAndView;
        }
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);

        // Check if the current user is the owner of the post
        if (!post.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to edit this post.");
        }

        postService.updatePost(id, updatePost);
        return new ModelAndView("redirect:/home");
    }


    @PostMapping("/{id}/like")
    public ModelAndView likePost(@PathVariable UUID id) {
        postService.likePost(id);
        return new ModelAndView("redirect:/home");
    }

    @PostMapping("/{id}/share")
    public ModelAndView sharePost(@PathVariable UUID id) {
        postService.sharePost(id);
        return new ModelAndView("redirect:/home");
    }
    @PostMapping("/{id}/rate")
    public ModelAndView ratePost(@PathVariable UUID id, @RequestParam int rating,
                                 @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());

        postService.ratePost(id, user, rating);

        return new ModelAndView("redirect:/home");
    }

    @PostMapping("/{id}/comment")
    public ModelAndView addComment(@PathVariable UUID id, @RequestParam String content,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.addComment(id, user, content);
        return new ModelAndView("redirect:/home");  // Redirect to the post page after the comment is added
    }

    @DeleteMapping("/{commentId}")
    public ModelAndView deleteComment(@PathVariable UUID commentId) {
        postService.deleteCommentFromPost(commentId);
        return new ModelAndView("redirect:/home");  // Redirect to home after deletion
    }

    @DeleteMapping("/{id}")
    public ModelAndView deletePost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);
        // Check if the current user is the owner of the post
        if (!post.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to delete this post.");
        }
        return new ModelAndView("redirect:/home");  // Redirect to home after deletion
    }
    


}