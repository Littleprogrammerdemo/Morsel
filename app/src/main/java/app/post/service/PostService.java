package app.post.service;

import app.category.model.Category;
import app.category.service.CategoryService;
import app.cloudinary.CloudinaryService;
import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.exception.CloudinaryException;
import app.post.model.Post;
import app.post.model.PostStatus;
import app.post.repository.PostRepository;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateNewPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, CommentService commentService,CloudinaryService cloudinaryService,
                       CategoryService categoryService, UserService userService) {
        this.postRepository = postRepository;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // Create a post with image upload
    public void createPost(User user, CreateNewPost createNewPost) {
        Category category = categoryService.getCategoryByType(createNewPost.getCategoryType());

        // Upload image to Cloudinary if file is provided
        String imageUrl = null;
        if (createNewPost.getImageFile() != null && !createNewPost.getImageFile().isEmpty()) {
            try {
                imageUrl = cloudinaryService.uploadRecipeImage(createNewPost.getImageFile(), "recipeId", 1);
            } catch (CloudinaryException e) {
                log.error("Failed to upload image to Cloudinary", e);
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        // Build post object
        Post post = Post.builder()
                .user(user)
                .title(createNewPost.getTitle())
                .content(createNewPost.getContent())
                .category(category)
                .imageUrl(imageUrl) // Store Cloudinary URL
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)
                .status(PostStatus.ACTIVE)
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
        return postRepository.findAll();
    }

    // Like a post
    public void likePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    // Share a post
    public void sharePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setShares(post.getShares() + 1);
        postRepository.save(post);
    }

    // Method to rate a post
    public void ratePost(UUID postId, User user, int rating) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating should be between 1 and 5.");
        }

        post.setRating(rating);
        postRepository.save(post);
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
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);  // Fetches all posts created by the specified user
    }

    public void deletePost(UUID postId, User user) {
        // Check if the post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the user is an admin or the post creator
        if (!isAdmin(user) && !isPostCreator(user, post)) {
            throw new RuntimeException("Only admins or creators can delete posts.");
        }

        // Delete the post
        postRepository.delete(post);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    private boolean isPostCreator(User user, Post post) {
        return user != null && post != null && user.getId().equals(post.getUser().getId());
    }


}

