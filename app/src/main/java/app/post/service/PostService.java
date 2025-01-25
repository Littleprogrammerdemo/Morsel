package app.post.service;

import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.like.service.LikeService;
import app.rating.service.RatingService;
import app.post.model.Post;
import app.post.repository.PostRepository;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final RatingService ratingService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, CommentService commentService,
                       LikeService likeService,
                       RatingService ratingService,
                       UserService userService) {
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.likeService = likeService;
        this.ratingService = ratingService;
        this.userService = userService;
    }

    // Create a post
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

    // Get a post by ID
    public Post getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll(); // This fetches all posts from the database
    }

    // Get comments for a post
    public List<Comment> getCommentsForPost(UUID postId) {
        return commentService.getCommentsForPost(postId);
    }

    // Add a comment to a post
    public void addComment(UUID postId, User user, String content) {
        Post post = getPostById(postId);
        commentService.addComment(post, user, content);
    }

    // Delete a comment from post
    public void deleteCommentFromPost(UUID commentId) {
        commentService.deleteComment(commentId); // Calling deleteComment from CommentService
    }

    // Like a post
    public void likePost(UUID postId, User user) {
        Post post = getPostById(postId);
        likeService.addLike(post, user);
        post.setLikes(post.getLikes() + 1); // Increment like count
        postRepository.save(post);
    }

    // Rate a post
    public void ratePost(UUID postId, double rating, User user) {
        Post post = getPostById(postId);

        // Call RatingService to add or update the rating
        ratingService.ratePost(post, user, rating);

        // Calculate the new average rating for the post
        double newRating = ratingService.calculateAverageRating(postId);
        post.setRating(newRating);

        // Save the updated post to the database
        postRepository.save(post);
    }

    // Delete a post
    public boolean deletePostById(UUID postId) {
        postRepository.deleteById(postId);
        return true;
    }

    // Upload image logic (implement as needed)
    public String uploadImage(MultipartFile image) throws IOException {
        // Handle image upload logic (store it on disk, cloud storage, etc.)
        return "image-url"; // Example URL to the image
    }

    // Save post to the repository
    public void savePost(Post post) {
        postRepository.save(post);
    }
}
