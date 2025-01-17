package app.web.dto;

import app.comment.repository.CommentRepository;
import app.post.repository.PostRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class PostCommentMediator {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostCommentMediator(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

}
