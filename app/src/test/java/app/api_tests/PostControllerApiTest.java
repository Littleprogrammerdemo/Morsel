package app.api_tests;

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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerApiTest {

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    // Test Share Post (POST /posts/{id}/share)
    @Test
    void shouldRedirectToHomeWhenAuthorizedUserCreatesPost() throws Exception {
        UUID postId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "AdminUser", "password", UserRole.ADMIN, true);

        // Setting up the security context to simulate an authenticated user with the correct role
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(post("/posts/{id}/share", postId)
                        .with(csrf())) // CSRF protection
                .andExpect(status().is3xxRedirection()) // Expect a redirection
                .andExpect(redirectedUrl("/home")); // Expect redirection to home

        verify(postService, times(1)).sharePost(any());
    }

    @Test
    void shouldRedirectToHomeWhenAuthorizedUserLikesPost() throws Exception {
        UUID postId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "AdminUser", "password", UserRole.ADMIN, true);

        // Setting up the security context to simulate an authenticated user with the correct role
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(post("/posts/{id}/like", postId)
                        .with(csrf())) // CSRF protection
                .andExpect(status().is3xxRedirection()) // Expect a redirection
                .andExpect(redirectedUrl("/home")); // Expect redirection to home

        verify(postService, times(1)).likePost(any(), any());
    }
    @Test
    void shouldRedirectToHomeWhenAuthorizedUserAddsComment() throws Exception {
        UUID postId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "AdminUser", "password", UserRole.ADMIN, true);

        // Setting up the security context to simulate an authenticated user with the correct role
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(post("/posts/{id}/comment", postId)
                        .with(csrf()) // CSRF protection
                        .param("content", "This is a comment"))
                .andExpect(status().is3xxRedirection()) // Expect a redirection
                .andExpect(redirectedUrl("/home")); // Expect redirection to home

        verify(postService, times(1)).addComment(any(), any(), any());
    }


}
