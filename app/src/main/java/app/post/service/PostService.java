package app.post.service;

import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.post.model.Post;
import app.post.repository.PostRepository;
import app.user.model.User;
import app.user.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class PostService {
    private final PostRepository postRepository;

    private final CommentService commentService;  // Inject CommentService
    @Getter
    private final UserService userService;

    public PostService(PostRepository postRepository, CommentService commentService, UserService userService) {
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.userService = userService;
    }

    public void createPost(User user, String title, String content) {
        Post post = Post.builder()
                .owner(user)
                .title(title)
                .content(content)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)  // Default rating is 0
                .build();
        postRepository.save(post);
    }

    public List<Post> getPostsByUser(UUID userId) {
        return postRepository.findByOwnerId(userId);
    }

    public Post getPostById(UUID postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Get comments for a post
    public List<Comment> getCommentsForPost(UUID postId) {

        return commentService.getCommentsByPost(postId);
    }

    public Post likePost(UUID postId) {
        Post post = getPostById(postId);
        post.setLikes(post.getLikes() + 1);  // Increment the like count
        return postRepository.save(post);
    }

    public Post ratePost(UUID postId, double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0 stars.");
        }

        Post post = getPostById(postId);
        double newRating = (post.getRating() + rating) / 2;  // Calculate new average rating
        post.setRating(newRating);
        return postRepository.save(post);
    }
    public List<Post> getAllPosts() {
        return postRepository.findAll();  // Fetching all posts from the repository
    }

    // Method to delete a post by its ID
    public void deletePostById(UUID postId) {
        // Use the repository to delete the post by its ID
        postRepository.deleteById(postId);
    }

}
