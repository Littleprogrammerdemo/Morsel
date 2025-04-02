package app.unit_tests.posts;

import app.post.model.Post;
import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.PostController;
import app.web.dto.CreateNewPost;
import app.web.dto.UpdatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerUnitTest {

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    @Mock
    private AuthenticationMetadata authenticationMetadata;

    @Mock
    private BindingResult bindingResult;

    private User user;
    private UUID postId;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        postId = UUID.randomUUID();
        post = new Post();
        post.setId(postId);
        post.setTitle("Sample Title");
        post.setContent("Sample Content");
    }

    @Test
    void shouldReturnAllPostsViewWithUserAndPosts() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);
        when(postService.getPostsByUser(user)).thenReturn(List.of(post));

        ModelAndView modelAndView = postController.getAllPosts(authenticationMetadata);

        assertEquals("posts", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(List.of(post), modelAndView.getModel().get("posts"));
    }

    @Test
    void shouldReturnCreatePostFormViewWithUser() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.createPostForm(authenticationMetadata);

        assertEquals("createPost", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertTrue(modelAndView.getModel().get("createNewPost") instanceof CreateNewPost);
    }

    @Test
    void shouldCreatePostWhenValidData() {
        CreateNewPost createNewPost = new CreateNewPost();
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

        ModelAndView modelAndView = postController.createPost(createNewPost, bindingResult, authenticationMetadata);

        verify(postService, times(1)).createPost(user, createNewPost);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

    @Test
    void shouldReturnFormWhenInvalidData() {
        CreateNewPost createNewPost = new CreateNewPost();
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView modelAndView = postController.createPost(createNewPost, bindingResult, authenticationMetadata);

        assertEquals("createPost", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(createNewPost, modelAndView.getModel().get("createNewPost"));
    }

    @Test
    void shouldHandleEditingNonExistentPost() {
        when(postService.getPostById(postId)).thenThrow(new RuntimeException("Post not found"));

        assertThrows(RuntimeException.class, () -> postController.editPostForm(postId, authenticationMetadata));
    }


    @Test
    void shouldLikePostAndRedirect() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.likePost(postId, authenticationMetadata);
        verify(postService, times(1)).likePost(postId, user);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

    @Test
    void shouldSharePostAndRedirect() {
        ModelAndView modelAndView = postController.sharePost(postId);
        verify(postService, times(1)).sharePost(postId);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

    @Test
    void shouldRatePostAndRedirect() {
        int rating = 5;
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.ratePost(postId, rating, authenticationMetadata);
        verify(postService, times(1)).ratePost(postId, user, rating);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }
    @Test
    void shouldDeleteCommentFromPost() {
        UUID commentId = UUID.randomUUID();

        ModelAndView modelAndView = postController.deleteComment(commentId);

        verify(postService, times(1)).deleteCommentFromPost(commentId);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

    @Test
    void shouldAddCommentToPost() {
        String content = "This is a comment.";

        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.addComment(postId, content, authenticationMetadata);

        verify(postService, times(1)).addComment(postId, user, content);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

}
