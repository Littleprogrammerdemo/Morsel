package app.comment.service;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import app.exception.CommentNotFoundException;
import app.post.model.Post;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {

        this.commentRepository = commentRepository;
    }
    public void addComment(Post post, User user, String content) {
        Comment comment = Comment.builder()
                .post(post)  // Associate the comment with the post
                .owner(user)  // Associate the comment with the user
                .content(content)
                .createdOn(LocalDateTime.now())
                .build();

        // Save the comment in the repository
        commentRepository.save(comment);
    }
    public List<Comment> getCommentsForPost(UUID postId) {
        return commentRepository.findByPost_Id(postId);
    }

    // Изтриване на коментар
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }
}
