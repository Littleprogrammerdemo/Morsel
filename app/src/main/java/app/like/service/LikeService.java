package app.like.service;

import app.like.model.Like;
import app.like.repository.LikeRepository;
import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostService postService;

    public void likePost(UUID postId, User user) {
        Post post = postService.getPostById(postId);
        if (!likeRepository.findByPostAndUser(post, user).isPresent()) {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            postService.likePost(postId);  // Increment the like count
        }
    }
}
