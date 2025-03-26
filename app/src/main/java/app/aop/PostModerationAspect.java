package app.aop;

import app.post.model.Post;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Aspect
@Component
public class PostModerationAspect {

    private static final Set<String> offensiveWords = new HashSet<>(Arrays.asList("badword1", "badword2"));

    @Before("execution(* app.post.service.PostService.createPost(..)) && args(userId, post)")
    public void checkPostContent(UUID userId, Post post) {
        for (String word : offensiveWords) {
            if (post.getContent().contains(word)) {
                throw new IllegalArgumentException("Your post contains offensive language and cannot be posted.");
            }
        }
    }
}