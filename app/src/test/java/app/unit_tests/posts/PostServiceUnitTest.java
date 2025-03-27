package app.unit_tests.posts;

import app.category.model.CategoryType;
import app.comment.model.Comment;
import app.comment.service.CommentService;
import app.post.model.Post;
import app.post.model.PostStatus;
import app.post.repository.PostRepository;
import app.post.service.PostService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateNewPost;
import app.web.dto.UpdatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;


    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;
    private UUID postId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.USER);

        postId = UUID.randomUUID();
        post = new Post();
        post.setId(postId);
        post.setUser(user);
        post.setTitle("Sample Post");
        post.setContent("Sample Content");
        post.setCategoryType(CategoryType.MEXICAN);
        post.setCreatedOn(LocalDateTime.now());
        post.setUpdatedOn(LocalDateTime.now());
        post.setStatus(PostStatus.ACTIVE);
    }

    @Test
    void givenValidPostRequest_whenCreatePost_thenPostIsSaved() {
        CreateNewPost request = new CreateNewPost("Title", "Content", "MAIN_DISH", null);
        when(postRepository.save(any())).thenReturn(post);

        postService.createPost(user, request);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void givenValidId_whenGetPostById_thenReturnPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Post foundPost = postService.getPostById(postId);
        assertEquals(postId, foundPost.getId());
    }

    @Test
    void givenInvalidId_whenGetPostById_thenThrowException() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPostById(postId));
    }


    @Test
    void givenValidPost_whenSharePost_thenIncrementShares() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        post.setShares(0);
        postService.sharePost(postId);
        assertEquals(1, post.getShares());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void givenValidRating_whenRatePost_thenSetRating() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.ratePost(postId, user, 5);
        assertEquals(5, post.getRating());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void givenInvalidRating_whenRatePost_thenThrowException() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        assertThrows(IllegalArgumentException.class, () -> postService.ratePost(postId, user, 6));
    }

    @Test
    void givenValidUser_whenDeletePost_thenPostIsDeleted() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.deletePost(postId, user);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void givenNonCreatorOrAdmin_whenDeletePost_thenThrowException() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        assertThrows(RuntimeException.class, () -> postService.deletePost(postId, anotherUser));
    }

    @Test
    void givenAdminUser_whenDeletePost_thenDeleteSuccessfully() {
        User adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setRole(UserRole.ADMIN);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, adminUser);

        verify(postRepository, times(1)).delete(post);
    }


    @Test
    void whenGetAllPosts_thenReturnPosts() {
        when(postRepository.findAll()).thenReturn(List.of(post));
        List<Post> posts = postService.getAllPosts();
        assertEquals(1, posts.size());
    }

    @Test
    void whenGetCommentsForPost_thenReturnComments() {
        Comment comment = new Comment();
        when(commentService.getCommentsForPost(postId)).thenReturn(List.of(comment));
        List<Comment> comments = postService.getCommentsForPost(postId);
        assertEquals(1, comments.size());
    }

    @Test
    void whenDeleteComment_thenCommentIsDeleted() {
        doNothing().when(commentService).deleteComment(any());
        postService.deleteCommentFromPost(postId);
        verify(commentService, times(1)).deleteComment(any());
    }

    @Test
    void whenGetPostsByUser_thenReturnUserPosts() {
        when(postRepository.findByUser(user)).thenReturn(List.of(post));
        List<Post> userPosts = postService.getPostsByUser(user);
        assertEquals(1, userPosts.size());
    }

    @Test
    void givenNonExistingPost_whenDeletePost_thenThrowException() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.deletePost(postId, user));
    }

    @Test
    void givenNullUser_whenDeletePost_thenThrowException() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        assertThrows(RuntimeException.class, () -> postService.deletePost(postId, null));
    }

    @Test
    void givenDifferentUserAndNotAdmin_whenDeletePost_thenThrowException() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        assertThrows(RuntimeException.class, () -> postService.deletePost(postId, anotherUser));
    }

}