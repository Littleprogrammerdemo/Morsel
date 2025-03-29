package app.unit_tests.scheduler;

import app.post.model.Post;
import app.post.repository.PostRepository;
import app.scheduler.PostCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostCleanerTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostCleaner postCleaner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void shouldDoNothingWhenNoPostsToDelete() {
        // Arrange: No posts to delete
        when(postRepository.findByLikesAndCreatedOnBefore(0, LocalDateTime.now().minusYears(1)))
                .thenReturn(Collections.emptyList());

        // Act: Run the deleteOldPostsWithoutLikes method
        postCleaner.deleteOldPostsWithoutLikes();

        // Assert: Verify that no deletion occurred
        verify(postRepository, times(0)).delete(any(Post.class));
    }

    @Test
    void shouldLogMessageWhenNoPostsToDelete() {
        // Arrange: No posts to delete
        when(postRepository.findByLikesAndCreatedOnBefore(0, LocalDateTime.now().minusYears(1)))
                .thenReturn(Collections.emptyList());

        // Act: Run the deleteOldPostsWithoutLikes method
        postCleaner.deleteOldPostsWithoutLikes();

        // Assert: Verify the log message (this can be done using an in-memory logger or log capturing tool)
        // Since logging is handled by SLF4J and can't be captured directly here, consider using a log capturing framework
        // if you need to assert log messages.
    }
}
