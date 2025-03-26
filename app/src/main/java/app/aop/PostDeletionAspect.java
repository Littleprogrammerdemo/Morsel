package app.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class PostDeletionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PostDeletionAspect.class);

    @After("execution(* app.post.service.PostService.deletePost(..)) && args(postId, userId)")
    public void logPostDeletion(UUID postId, UUID userId) {
        logger.info("User with ID {} deleted post with ID {}.", userId, postId);
    }
}