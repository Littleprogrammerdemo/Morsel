
package app.post.service;

import app.category.model.Category;
import app.category.model.CategoryType;
import app.category.repository.CategoryRepository;
import app.category.service.CategoryService;
import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.like.service.LikeService;
import app.post.model.PostStatus;
import app.rating.service.RatingService;
import app.post.model.Post;
import app.post.repository.PostRepository;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
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
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, CommentService commentService,
                       LikeService likeService,
                       RatingService ratingService, CategoryService categoryService,
                       UserService userService) {
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.likeService = likeService;
        this.ratingService = ratingService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // Create a post
    public void createPost(User user, String title, String content, CategoryType category, MultipartFile imageFile) {
        byte[] imageData = null;

        // Process the image if it's provided
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                imageData = imageFile.getBytes();  // Convert the image to a byte array
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing image file", e);
        }
        Post post = Post.builder()
                .owner(user)
                .title(title)
                .content(content)
                .category_type(category)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)  // Default rating is 0
                .image(imageData)  // Save the image in the post
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
    public List<Post> searchPosts(String keyword) {
        return postRepository.searchPosts(keyword);
    }

    public List<Post> filterByCategory(Category category) {
        return postRepository.findByCategory(category);
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

    // Check if the user is an admin
    private boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    // Delete post (only Admin can delete)
    public void deletePost(UUID postId, User user) {
        if (!isAdmin(user)) {
            throw new RuntimeException("Only admins can delete posts.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Delete the post
        postRepository.delete(post);
    }

    // Update the status of a post
    public void updatePostStatus(UUID postId, PostStatus status) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setStatus(status);
        postRepository.save(post);
    }
    // Check if user is the creator of the post
    private boolean isPostCreator(User user, Post post) {
        return user != null && post != null && user.getId().equals(post.getOwner());
    }
    // Delete post (only Admin or Creator can delete)
    public void deletePostByUser(UUID postId, User user) {
        if (!isAdmin(user) && !isPostCreator(user, postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found")))) {
            throw new RuntimeException("Only admins or creators can delete posts.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Delete the post
        postRepository.delete(post);
    }


    // Upload image logic
    public String uploadImage(MultipartFile image) throws IOException {
        // Handle image upload logic (store it on disk, cloud storage, etc.)
        return "image-url"; // Example URL to the image
    }
    public Post addRecipeToCategory(UUID categoryId, Post recipe) {
        Category category = categoryService.getCategoryById(categoryId); // No need for orElseThrow
        recipe.setCategory(category);
        return postRepository.save(recipe);
    }

    // Save post to the repository
    public void savePost(Post post) {
        postRepository.save(post);
    }
}
