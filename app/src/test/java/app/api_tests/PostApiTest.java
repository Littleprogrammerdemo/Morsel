package app.api_tests;

import app.post.model.Post;
import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.controller.PostController;
import app.web.dto.CreateNewPost;
import app.web.dto.UpdatePostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostApiTest {

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllPosts_whenAccessingPostsPage() throws Exception {
        List<Post> posts = List.of(new Post(UUID.randomUUID(), "Test Post", "Content"));
        when(postService.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/posts").with(user(new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true))))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void shouldReturnPostDetails_whenViewingSpecificPost() throws Exception {
        UUID postId = UUID.randomUUID();
        Post post = new Post(postId, "Test Post", "Content");
        when(postService.getPostById(postId)).thenReturn(post);

        mockMvc.perform(get("/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("post", post));
    }

    @Test
    void shouldCreatePost_whenValidDataIsSubmitted() throws Exception {
        CreateNewPost createNewPost = new CreateNewPost("Title", "Content", "Category");
        mockMvc.perform(post("/posts")
                        .contentType("application/x-www-form-urlencoded")
                        .param("title", createNewPost.getTitle())
                        .param("content", createNewPost.getContent())
                        .param("category", createNewPost.getCategoryType())
                        .with(csrf())
                        .with(user(new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        verify(postService, times(1)).createPost(any(), any());
    }

    @Test
    void shouldUpdatePost_whenValidDataIsSubmitted() throws Exception {
        UUID postId = UUID.randomUUID();
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("Updated Title", "Updated Content", "Category", null);

        mockMvc.perform(post("/posts/{id}/update", postId)
                        .contentType("application/x-www-form-urlencoded")
                        .param("title", updatePostRequest.getTitle())
                        .param("content", updatePostRequest.getContent())
                        .param("category", updatePostRequest.getCategoryType())
                        .with(csrf())
                        .with(user(new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(postService, times(1)).updatePost(postId, updatePostRequest);
    }

    @Test
    void shouldDeletePost_whenPostIdIsValid() throws Exception {
        UUID postId = UUID.randomUUID();
        mockMvc.perform(delete("/posts/{id}", postId)
                        .with(csrf())
                        .with(user(new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(postService, times(1)).deletePost(postId, any());
    }

    @Test
    void shouldAddComment_whenValidDataIsSubmitted() throws Exception {
        UUID postId = UUID.randomUUID();
        String comment = "This is a comment!";
        mockMvc.perform(post("/posts/{id}/comment", postId)
                        .param("content", comment)
                        .with(csrf())
                        .with(user(new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(postService, times(1)).addComment(postId, any(), eq(comment));
    }

    @Test
    void shouldLikePost_whenLikeButtonIsClicked() throws Exception {
        UUID postId = UUID.randomUUID();
        mockMvc.perform(post("/posts/{id}/like", postId)
                        .with(csrf())
                        .with(user(new AuthenticationMetadata(UUID.randomUUID(), "TestUser", "password", UserRole.USER, true))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(postService, times(1)).likePost(postId);
    }
}
