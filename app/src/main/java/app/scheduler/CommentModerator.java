package app.scheduler;

import app.comment.model.Comment;
import app.comment.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CommentModerator {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentModerator(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    private static final List<String> BANNED_WORDS = Arrays.asList("spam", "fake", "offensive");
    // For testing - fixedRate = 10000
    @Scheduled(cron = "0 */15 * * * ?")  // Runs every 15 minutes
    public void moderateComments() {
        LocalDateTime last15Minutes = LocalDateTime.now().minusMinutes(15);
        List<Comment> recentComments = commentRepository.findByCreatedOnAfter(last15Minutes);

        if (recentComments.isEmpty()) {
            log.info("No new comments to moderate.");
            return;
        }

        for (Comment comment : recentComments) {
            for (String word : BANNED_WORDS) {
                if (comment.getContent().toLowerCase().contains(word)) {
                    comment.setFlagged(true);
                    commentRepository.save(comment);
                    log.info("Flagged comment: " + comment.getContent());
                }
            }
        }
    }
}
