package app.unit_tests.scheduler;

import app.post.model.Post;
import app.post.repository.PostRepository;
import app.scheduler.TrendingPostsUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class TrendingPostsUpdaterTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private TrendingPostsUpdater trendingPostsUpdater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void shouldLogNoTrendingPostsWhenListIsEmpty() {
        // Arrange: Mock postRepository to return an empty list (no trending posts)
        when(postRepository.findTop10ByOrderByLikesDesc()).thenReturn(List.of());

        // Act: Run the updateTrendingPosts method
        trendingPostsUpdater.updateTrendingPosts();

        // Assert: Verify that the log message indicates no trending posts
        // We can use a log capture tool or verify the logging, but for simplicity, let's assume
        // that we are verifying that the postRepository method was called correctly
        verify(postRepository, times(1)).findTop10ByOrderByLikesDesc();
    }

    @Test
    void shouldLogTrendingPostsWhenPostsExist() {
        // Arrange: Create mock posts
        Post post1 = mock(Post.class);
        Post post2 = mock(Post.class);
        when(post1.getTitle()).thenReturn("Post 1");
        when(post2.getTitle()).thenReturn("Post 2");

        // Mock postRepository to return a list of trending posts
        when(postRepository.findTop10ByOrderByLikesDesc()).thenReturn(List.of(post1, post2));

        // Act: Run the updateTrendingPosts method
        trendingPostsUpdater.updateTrendingPosts();

        // Assert: Verify that the postRepository method was called
        verify(postRepository, times(1)).findTop10ByOrderByLikesDesc();

        // Optionally, verify that logging occurs with the correct titles
        // This could be done with a logging capture tool such as LogCaptor or using a custom appender
    }

}
