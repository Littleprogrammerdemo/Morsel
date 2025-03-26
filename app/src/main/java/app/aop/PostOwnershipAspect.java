package app.aop;

import app.post.model.Post;
import app.post.service.PostService;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class PostOwnershipAspect {

    @Autowired
    private PostService postService;

    @Before("execution(* app.post.service.PostService.deletePost(..)) && args(postId, userId)")
    public void checkPostOwnership(UUID postId, UUID userId) {
        Post post = postService.getPostById(postId);
        if (post == null || !post.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only delete your own posts!");
        }
    }
}
