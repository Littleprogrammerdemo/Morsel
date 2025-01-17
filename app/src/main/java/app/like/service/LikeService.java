package app.like.service;

import app.like.model.Like;
import app.like.repository.LikeRepository;
import app.post.model.Post;
import app.post.repository.PostRepository;
import app.post.service.PostService;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
                    .owner(user)
                    .build();
            likeRepository.save(like);
            postService.likePost(postId);  // Increment the like count
        }
    }

    public int getLikesCount(UUID postId) {
        Post post = postService.getPostById(postId);
        return likeRepository.countByPost(post);  // Broi vsichki laykove za tozi post
    }
    public void unlikePost(UUID postId, User user) {
        // Retrieve the post by postId
        Post post = postService.getPostById(postId);

        // Find the Like entity for the given post and user
        Optional<Like> likeOpt = likeRepository.findByPostAndUser(post, user);

        // If a like exists, remove it
        if (likeOpt.isPresent()) {
            // Remove the like from the repository
            likeRepository.delete(likeOpt.get());

            // Decrement the like count on the post
            post.setLikes(post.getLikes() - 1);  // Decrement the like count
            postService.likePost(postId);;  // Save the updated post
        }
    }
}
