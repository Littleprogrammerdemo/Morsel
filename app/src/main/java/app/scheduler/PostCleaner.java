    package app.scheduler;

    import app.post.model.Post;
    import app.post.repository.PostRepository;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Component;

    import java.time.LocalDateTime;
    import java.util.List;

    @Slf4j
    @Component
    public class PostCleaner {

        private final PostRepository postRepository;

        @Autowired
        public PostCleaner(PostRepository postRepository) {
            this.postRepository = postRepository;
        }
        // For testing - fixedRate = 10000
        // This task will run every day at midnight
        @Scheduled(cron = "0 0 0 * * ?")  // Every day at 00:00
        public void deleteOldPostsWithoutLikes() {
            // Date one year ago (as LocalDateTime)
            LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

            // Find posts with no likes and created before one year
            List<Post> postsToDelete = postRepository.findByLikesAndCreatedOnBefore(0, oneYearAgo);

            if (postsToDelete.isEmpty()) {
                log.info("No posts found for deletion.");
                return;
            }

            // Delete these posts from the database
            for (Post post : postsToDelete) {
                postRepository.delete(post);
                log.info("Deleted post: " + post.getTitle());
            }
        }
    }