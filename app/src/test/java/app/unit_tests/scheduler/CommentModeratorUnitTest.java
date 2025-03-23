package app.unit_tests.scheduler;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import app.scheduler.CommentModerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentModeratorUnitTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentModerator commentModerator;

    private Comment cleanComment;
    private Comment spamComment;

    @BeforeEach
    void setUp() {
        cleanComment = new Comment();
        cleanComment.setId(UUID.randomUUID());
        cleanComment.setContent("This is a normal comment.");
        cleanComment.setCreatedOn(LocalDateTime.now());
        cleanComment.setFlagged(false);

        spamComment = new Comment();
        spamComment.setId(UUID.randomUUID());
        spamComment.setContent("This is spam!");
        spamComment.setCreatedOn(LocalDateTime.now());
        spamComment.setFlagged(false);
    }

    @Test
    void shouldNotFlagCleanComments() {
        when(commentRepository.findByCreatedOnAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(cleanComment));

        commentModerator.moderateComments();

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldFlagSpamComments() {
        when(commentRepository.findByCreatedOnAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(spamComment));

        commentModerator.moderateComments();

        verify(commentRepository, times(1)).save(spamComment);
        assert spamComment.isFlagged();
    }

    @Test
    void shouldLogNoNewCommentsWhenListIsEmpty() {
        when(commentRepository.findByCreatedOnAfter(any(LocalDateTime.class)))
                .thenReturn(List.of());

        commentModerator.moderateComments();

        verify(commentRepository, never()).save(any(Comment.class));
    }
}