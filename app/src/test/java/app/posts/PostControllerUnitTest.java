package app.posts;

import app.post.model.Post;
import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.PostController;
import app.web.dto.CreateNewPost;
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
    }

    @Test
    void shouldReturnAllPostsViewWithUserAndPosts() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);
        when(postService.getAllPosts()).thenReturn(List.of(post));

        ModelAndView modelAndView = postController.getAllPosts(authenticationMetadata);

        assertEquals("posts", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(List.of(post), modelAndView.getModel().get("post"));
    }

    @Test
    void shouldReturnPostViewWithPostAndComments() {
        when(postService.getPostById(postId)).thenReturn(post);
        when(postService.getCommentsForPost(postId)).thenReturn(List.of());

        ModelAndView modelAndView = postController.viewPost(postId);

        assertEquals("posts", modelAndView.getViewName());
        assertEquals(post, modelAndView.getModel().get("post"));
        assertEquals(List.of(), modelAndView.getModel().get("comments"));
    }

    @Test
    void shouldReturnCreatePostFormViewWithUser() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.createPostForm(authenticationMetadata);

        assertEquals("createRecipe", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertTrue(modelAndView.getModel().get("createRecipe") instanceof CreateNewPost);
    }

    @Test
    void shouldCreatePostWhenValidData() {
        CreateNewPost createNewPost = new CreateNewPost();
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

        ModelAndView modelAndView = postController.createPost(createNewPost, bindingResult, authenticationMetadata);

        verify(postService, times(1)).createPost(user, createNewPost);
        assertEquals("redirect:/posts", modelAndView.getViewName());
    }

    @Test
    void shouldReturnFormWhenInvalidData() {
        CreateNewPost createNewPost = new CreateNewPost();
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView modelAndView = postController.createPost(createNewPost, bindingResult, authenticationMetadata);

        assertEquals("createRecipe", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(createNewPost, modelAndView.getModel().get("createRecipe"));
    }

    @Test
    void shouldLikePostAndRedirect() {
        ModelAndView modelAndView = postController.likePost(postId);
        verify(postService, times(1)).likePost(postId);
        assertEquals("redirect:/posts/" + postId, modelAndView.getViewName());
    }

    @Test
    void shouldSharePostAndRedirect() {
        ModelAndView modelAndView = postController.sharePost(postId);
        verify(postService, times(1)).sharePost(postId);
        assertEquals("redirect:/posts/" + postId, modelAndView.getViewName());
    }

    @Test
    void shouldRatePostAndRedirect() {
        int rating = 5;
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.ratePost(postId, rating, authenticationMetadata);
        verify(postService, times(1)).ratePost(postId, user, rating);
        assertEquals("redirect:/posts/" + postId, modelAndView.getViewName());
    }

    @Test
    void shouldAddCommentAndRedirect() {
        String comment = "Nice post!";
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.addComment(postId, comment, authenticationMetadata);
        verify(postService, times(1)).addComment(postId, user, comment);
        assertEquals("redirect:/posts/" + postId, modelAndView.getViewName());
    }

    @Test
    void shouldDeleteCommentAndRedirect() {
        UUID commentId = UUID.randomUUID();
        ModelAndView modelAndView = postController.deleteComment(commentId);
        verify(postService, times(1)).deleteCommentFromPost(commentId);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }

    @Test
    void shouldDeletePostAndRedirect() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.deletePost(postId, authenticationMetadata);
        verify(postService, times(1)).deletePost(postId, user);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }
}