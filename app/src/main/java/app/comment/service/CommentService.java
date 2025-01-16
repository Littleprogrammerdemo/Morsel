package app.comment.service;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    public Comment addComment(UUID postId, User user, String content) {
        Post post = postService.getPostById(postId);
        Comment comment = Comment.builder()
                .post(post)
                .owner(user)
                .content(content)
                .createdOn(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(UUID postId) {
        Post post = postService.getPostById(postId);
        return commentRepository.findByPost(post);

    }
}
