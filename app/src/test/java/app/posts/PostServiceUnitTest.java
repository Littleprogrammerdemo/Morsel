package app.posts;

import app.category.model.CategoryType;
import app.cloudinary.CloudinaryService;
import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.exception.CloudinaryException;
import app.post.model.Post;
import app.post.model.PostStatus;
import app.post.repository.PostRepository;
import app.post.service.PostService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateNewPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    // Service to be tested
    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;
    private UUID postId;
    private UUID userId;

    // Set up mock data before each test
    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .role(UserRole.USER)
                .build();

        testPost = Post.builder()
                .id(postId)
                .user(testUser)
                .title("Test Post")
                .content("This is a test post.")
                .categoryType(CategoryType.MAIN_DISH)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .likes(0)
                .rating(0)
                .status(PostStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldCreatePostSuccessfully() {
        CreateNewPost newPost = new CreateNewPost();
        newPost.setTitle("New Post");
        newPost.setContent("Post content");
        newPost.setCategoryType("MAIN_DISH");

        // Mock the save method to return a post
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Verify no exceptions thrown
        assertDoesNotThrow(() -> postService.createPost(testUser, newPost));

        // Verify the save method is called once
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void shouldThrowExceptionWhenCloudinaryFails() throws CloudinaryException {
        CreateNewPost newPost = new CreateNewPost();
        newPost.setTitle("New Post");
        newPost.setContent("Post content");
        newPost.setCategoryType("ITALIAN");
        newPost.setImageFile(mock(org.springframework.web.multipart.MultipartFile.class));

        // Mock Cloudinary service to throw an exception
        when(cloudinaryService.uploadRecipeImage(any(), anyString(), anyInt())).thenThrow(new CloudinaryException("Upload failed"));

        // Expect exception and verify message
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.createPost(testUser, newPost));
        assertEquals("Failed to upload image", exception.getMessage());

        // Verify post is not saved due to failure
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldReturnPostById() {
        // Mock the findById method to return a post
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // Retrieve post and verify
        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(testPost.getId(), result.getId());
        assertEquals(testPost.getTitle(), result.getTitle());
    }

    @Test
    void shouldThrowExceptionIfPostNotFound() {
        // Mock the findById method to return an empty result
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Expect exception when post is not found
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.getPostById(postId));
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void shouldReturnAllPosts() {
        // Mock the findAll method to return a list of posts
        when(postRepository.findAll()).thenReturn(Arrays.asList(testPost));

        // Retrieve all posts and verify size
        List<Post> posts = postService.getAllPosts();

        assertNotNull(posts);
        assertEquals(1, posts.size());
    }

    @Test
    void shouldLikePost() {
        // Mock the findById method to return a post
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // Like the post
        postService.likePost(postId);

        // Verify like count increased and post is saved
        assertEquals(1, testPost.getLikes());
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    void shouldThrowExceptionWhenLikingNonexistentPost() {
        // Mock the findById method to return an empty result
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Expect exception when post does not exist
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.likePost(postId));
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void shouldRatePostSuccessfully() {
        // Mock the findById method to return a post
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // Rate the post and verify rating
        postService.ratePost(postId, testUser, 5);

        assertEquals(5, testPost.getRating());
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    void shouldThrowExceptionForInvalidRating() {
        // Mock the findById method to return a post
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // Expect exception for invalid rating
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.ratePost(postId, testUser, 6));
        assertEquals("Rating should be between 1 and 5.", exception.getMessage());

        // Verify save is not called for invalid rating
        verify(postRepository, never()).save(testPost);
    }

    @Test
    void shouldGetCommentsForPost() {
        // Mock the comment service to return a list of comments
        Comment comment = new Comment();
        when(commentService.getCommentsForPost(postId)).thenReturn(List.of(comment));

        // Retrieve comments and verify size
        List<Comment> comments = postService.getCommentsForPost(postId);

        assertEquals(1, comments.size());
        verify(commentService, times(1)).getCommentsForPost(postId);
    }

    @Test
    void shouldAddCommentToPost() {
        // Mock the findById method to return a post
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // Add a comment to the post
        postService.addComment(postId, testUser, "Nice post!");

        // Verify comment is added
        verify(commentService, times(1)).addComment(testPost, testUser, "Nice post!");
    }

    @Test
    void shouldDeleteCommentFromPost() {
        UUID commentId = UUID.randomUUID();

        // Delete comment and verify
        postService.deleteCommentFromPost(commentId);

        verify(commentService, times(1)).deleteComment(commentId);
    }

    @Test
    void shouldGetPostsByUser() {
        // Mock the findByUser method to return a list of posts
        when(postRepository.findByUser(testUser)).thenReturn(List.of(testPost));

        // Retrieve posts by user and verify
        List<Post> userPosts = postService.getPostsByUser(testUser);

        assertEquals(1, userPosts.size());
        assertEquals(testPost.getTitle(), userPosts.get(0).getTitle());
    }

    @Test
    void shouldDeletePostAsAdmin() {
        // Mock admin user and post deletion
        User adminUser = User.builder().id(UUID.randomUUID()).role(UserRole.ADMIN).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        postService.deletePost(postId, adminUser);

        // Verify the post is deleted
        verify(postRepository, times(1)).delete(testPost);
    }

    @Test
    void shouldDeletePostAsCreator() {
        // Mock creator user and post deletion
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        postService.deletePost(postId, testUser);

        // Verify the post is deleted
        verify(postRepository, times(1)).delete(testPost);
    }

    @Test
    void shouldThrowExceptionWhenDeletingPostWithoutPermission() {
        // Mock another user and expect exception for deleting post
        User anotherUser = User.builder().id(UUID.randomUUID()).role(UserRole.USER).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.deletePost(postId, anotherUser));
        assertEquals("Only admins or creators can delete posts.", exception.getMessage());

        // Verify post is not deleted
        verify(postRepository, never()).delete(testPost);
    }
}
