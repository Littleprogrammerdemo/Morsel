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
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        List<Post> posts = postService.getPostsByUser(user);
        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("user", user);
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewPost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);
        List<Comment> comments = postService.getCommentsForPost(id);

        if (post == null) {
            throw new RuntimeException("Post not found with id: " + id);
        }


        ModelAndView modelAndView = new ModelAndView("postDetails");
        modelAndView.addObject("post", post);
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView createPostForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("createPost");
        modelAndView.addObject("createNewPost", new CreateNewPost());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createPost(@Valid CreateNewPost createNewPost, BindingResult bindingResult,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("createPost");
            modelAndView.addObject("createNewPost", createNewPost);
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        postService.createPost(user, createNewPost);
        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editPostForm(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);

        if (post == null) {
            throw new RuntimeException("Post not found.");
        }

        if (!post.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to edit this post.");
        }

        // Preparing the form with existing post data
        UpdatePostRequest updatePostRequest = new UpdatePostRequest(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategoryType().name(),
                post.getImageUrl()
        );

        ModelAndView modelAndView = new ModelAndView("editPost");
        modelAndView.addObject("updatePost", updatePostRequest);
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    @PostMapping("/{id}/update")
    public ModelAndView updatePost(@PathVariable UUID id, @Valid UpdatePostRequest updatePost,
                                   BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("editPost");
            modelAndView.addObject("updatePost", updatePost);  // This will re-populate the form with the entered values
            return modelAndView;
        }

        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);

        if (post == null) {
            throw new RuntimeException("Post not found.");
        }

        if (!post.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to edit this post.");
        }

        // Update the post in the database
        postService.updatePost(id, updatePost);

        // Redirect after successful update
        return new ModelAndView("redirect:/home");
    }


    @PostMapping("/{id}/like")
    public ModelAndView likePost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        postService.likePost(id, user);
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
        return new ModelAndView("redirect:/home");
    }

    @DeleteMapping("/{commentId}/delete")
    public ModelAndView deleteComment(@PathVariable UUID commentId) {
        postService.deleteCommentFromPost(commentId);
        return new ModelAndView("redirect:/home");
    }

    @DeleteMapping("/{id}/delete")
    public ModelAndView deletePost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        Post post = postService.getPostById(id);

        if (!post.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to delete this post.");
        }

        postService.deletePost(id, user);
        return new ModelAndView("redirect:/home");
    }
}
