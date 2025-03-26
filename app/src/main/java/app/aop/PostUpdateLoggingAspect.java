package app.aop;

import app.post.model.Post;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class PostUpdateLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(PostUpdateLoggingAspect.class);

    @After("execution(* app.post.service.PostService.updatePost(..)) && args(postId, userId, post)")
    public void logPostUpdate(UUID postId, UUID userId, Post post) {
        logger.info("User with ID {} updated post with ID {}. New content: {}", userId, postId, post.getContent());
    }
}
