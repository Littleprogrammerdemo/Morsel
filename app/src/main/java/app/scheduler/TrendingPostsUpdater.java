package app.scheduler;

import app.post.model.Post;
import app.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TrendingPostsUpdater {

    private final PostRepository postRepository;

    @Autowired
    public TrendingPostsUpdater(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    // For testing - fixedRate = 10000
    @Scheduled(cron = "0 */30 * * * ?")  // Runs every 30 minutes
    public void updateTrendingPosts() {
        List<Post> trendingPosts = postRepository.findTop10ByOrderByLikesDescViewsDesc();

        if (trendingPosts.isEmpty()) {
            log.info("No trending posts found.");
            return;
        }

        log.info("Updated trending posts: ");
        trendingPosts.forEach(post -> log.info(post.getTitle()));
    }
}
