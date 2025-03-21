package app.post.service;

import app.category.model.CategoryType;
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
import app.web.dto.UpdatePostRequest;
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
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, CommentService commentService,CloudinaryService cloudinaryService,
                        UserService userService) {
        this.postRepository = postRepository;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // Create a post with image upload
    public void createPost(User user, CreateNewPost createNewPost) {

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
                .categoryType(CategoryType.valueOf(createNewPost.getCategoryType()))
                .imageUrl(imageUrl) // Store Cloudinary URL
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)
                .status(PostStatus.ACTIVE)
                .build();

        postRepository.save(post);
    }
    // Update a post with new image, title, content, and category
    public void updatePost(UUID id, UpdatePostRequest updatePost) {
        Post post = getPostById(id);

        // Update fields if they are not null
        if (updatePost.getTitle() != null) post.setTitle(updatePost.getTitle());
        if (updatePost.getContent() != null) post.setContent(updatePost.getContent());
        if (updatePost.getCategoryType() != null)
            post.setCategoryType(CategoryType.valueOf(updatePost.getCategoryType()));

        // Handle image upload if a new image is provided
        if (updatePost.getImageFile() != null && !updatePost.getImageFile().isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadRecipeImage(updatePost.getImageFile(), "recipeId", 1);
                post.setImageUrl(imageUrl);  // Update the image URL
            } catch (CloudinaryException e) {
                log.error("Failed to upload image to Cloudinary", e);
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        // Update the timestamp of the post
        post.setUpdatedOn(LocalDateTime.now());

        // Save the updated post
        postRepository.save(post);
    }

    // Get a post by ID
    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Like a post
    public void likePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }
    // Increment view count for a post
    public void incrementView(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
    // Share a post
    public void sharePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setShares(post.getShares() + 1);
        postRepository.save(post);
    }

    // Method to rate a post
    public void ratePost(UUID id, User user, int rating) {
        Post post = postRepository.findById(id)
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
    public void addComment(UUID id, User user, String content) {
        Post post = getPostById(id);
        commentService.addComment(post, user, content);
    }

    // Delete a comment from post
    public void deleteCommentFromPost(UUID commentId) {
        commentService.deleteComment(commentId); // Calling deleteComment from CommentService
    }
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);  // Fetches all posts created by the specified user
    }

    public void deletePost(UUID id, User user) {
        // Check if the post exists
        Post post = postRepository.findById(id)
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

