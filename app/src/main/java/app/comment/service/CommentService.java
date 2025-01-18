package app.comment.service;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import app.exception.CommentNotFoundException;
import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    private final PostService postService;
    @Autowired
    public CommentService( CommentRepository commentRepository,
                           PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public void addComment(UUID postId, User user, String content) {
        Post post = postService.getPostById(postId);
        Comment comment = Comment.builder()
                .post(post)
                .owner(user)
                .content(content)
                .createdOn(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(UUID postId) {
        Post post = postService.getPostById(postId);
        return commentRepository.findByPost(post);

    }
    public void deleteCommentById(UUID commentId) {
        // Assuming commentRepository handles the data persistence
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        commentRepository.delete(comment);
    }


}