package app.comment;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import app.comment.service.CommentService;
import app.exception.CommentNotFoundException;
import app.post.model.Post;
import app.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceUnitTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private Post post;

    @Mock
    private User user;

    @Test
    void givenPostAndUser_whenAddComment_thenSaveComment() {
        // Given
        String content = "This is a comment";
        Comment comment = Comment.builder()
                .post(post)
                .owner(user)
                .content(content)
                .createdOn(LocalDateTime.now())
                .build();

        // When
        commentService.addComment(post, user, content);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void givenPostId_whenGetCommentsForPost_thenReturnComments() {
        // Given
        UUID postId = UUID.randomUUID();
        List<Comment> comments = List.of(new Comment(), new Comment());
        when(commentRepository.findByPost_Id(postId)).thenReturn(comments);

        // When
        List<Comment> result = commentService.getCommentsForPost(postId);

        // Then
        assertEquals(2, result.size());
        verify(commentRepository, times(1)).findByPost_Id(postId);
    }

    @Test
    void givenInvalidCommentId_whenDeleteComment_thenThrowException() {
        // Given
        UUID commentId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void givenValidCommentId_whenDeleteComment_thenDeleteComment() {
        // Given
        UUID commentId = UUID.randomUUID();
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(commentId);

        // Then
        verify(commentRepository, times(1)).delete(comment);
    }
}
