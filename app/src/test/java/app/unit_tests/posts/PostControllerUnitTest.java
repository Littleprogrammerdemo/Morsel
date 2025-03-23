package app.unit_tests.posts;

import app.category.model.CategoryType;
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
import java.util.Optional;
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

        assertEquals("createPost", modelAndView.getViewName());
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

        assertEquals("createPost", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(createNewPost, modelAndView.getModel().get("createRecipe"));
    }

    @Test
    void shouldUpdatePostWhenValidData() {
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("Updated Title", "Updated Content", "MEXICAN", null);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.updatePost(postId, updatePostRequest, bindingResult, authenticationMetadata);

        verify(postService, times(1)).updatePost(postId, updatePostRequest);
        assertEquals("redirect:/posts/" + postId, modelAndView.getViewName());
    }

    @Test
    void shouldReturnEditFormWhenUpdateHasErrors() {
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("Updated Title", "Updated Content", "MEXICAN", null);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView modelAndView = postController.updatePost(postId, updatePostRequest, bindingResult, authenticationMetadata);

        assertEquals("editPost", modelAndView.getViewName());
        assertEquals(updatePostRequest, modelAndView.getModel().get("updatePost"));
    }

    @Test
    void shouldHandleEditingNonExistentPost() {
        when(postService.getPostById(postId)).thenThrow(new RuntimeException("Post not found"));

        assertThrows(RuntimeException.class, () -> postController.editPostForm(postId, authenticationMetadata));
    }

    @Test
    void shouldHandleDeletingNonExistentPost() {
        doThrow(new RuntimeException("Post not found")).when(postService).deletePost(postId, user);

        assertThrows(RuntimeException.class, () -> postController.deletePost(postId, authenticationMetadata));
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
    void shouldDeletePostAndRedirect() {
        when(authenticationMetadata.getUserId()).thenReturn(user.getId());
        when(userService.getByUserId(user.getId())).thenReturn(user);

        ModelAndView modelAndView = postController.deletePost(postId, authenticationMetadata);
        verify(postService, times(1)).deletePost(postId, user);
        assertEquals("redirect:/home", modelAndView.getViewName());
    }
}
