package app.web.controller;

import app.comment.service.CommentService;
import app.post.service.PostService;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.attoparser.dom.Comment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/post/{postId}/comment")
    public String addComment(
            @PathVariable UUID postId,
            @AuthenticationPrincipal User user, Comment commentCommand) {

        log.debug("Adding comment to post with id: {}", postId);

        commentService.addComment(postId, user, commentCommand.getContent());

        // Redirect to the post page after the comment is added
        return "redirect:/post/" + postId;
    }

    @GetMapping("/comment/{commentId}/delete")
    public String deleteComment(@PathVariable UUID commentId) {
        log.debug("Deleting comment with id: {}", commentId);
        commentService.deleteCommentById(commentId);
        return "redirect:/home";  // Redirect to home after deletion
    }
}
