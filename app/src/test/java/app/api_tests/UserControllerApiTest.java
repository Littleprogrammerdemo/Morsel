package app.api_tests;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void putUnauthorizedRequestToSwitchRole_shouldReturn403Forbidden() throws Exception {

        // 1. Build Request
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "User123", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isForbidden())  // Change from .isNotFound() to .isForbidden()
                .andExpect(view().name("access-denied"));  // You might want to display an error view (e.g., "error" or "403")
    }

    @Test
    void putAuthorizedRequestToSwitchRole_shouldRedirectToUsers() throws Exception {

        // 1. Build Request
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "User123", "123123", UserRole.ADMIN, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-panel"));
        verify(userService, times(1)).changeUserRole(any());
    }
    @Test
    void putUnauthorizedRequestToSwitchStatus_shouldReturn403Forbidden() throws Exception {

        // 1. Build Request
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "User123", "123123", UserRole.USER, true);  // Non-admin user
        MockHttpServletRequestBuilder request = put("/users/{id}/status", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isForbidden())  // Forbidden because user is not an admin
                .andExpect(view().name("access-denied"));  // Display access denied view (could be 403 error page)
    }

    @Test
    void putAuthorizedRequestToSwitchStatus_shouldRedirectToAdminPanel() throws Exception {

        // 1. Build Request
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "AdminUser", "admin123", UserRole.ADMIN, true);  // Admin user
        MockHttpServletRequestBuilder request = put("/users/{id}/status", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())  // Redirect after successful status switch
                .andExpect(redirectedUrl("/admin-panel"));

        // Verify service method was called once
        verify(userService, times(1)).switchStatus(any());
    }

    @Test
    void putUnauthorizedRequestToUpdateProfile_shouldReturn403Forbidden() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "User123", "123123", UserRole.USER, true);  // Different user
        MockHttpServletRequestBuilder request = put("/users/{id}/profile", userId)
                .with(user(principal))
                .with(csrf())
                .contentType("application/json")
                .content("{ \"email\": \"newemail@example.com\", \"username\": \"NewUser\" }");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isForbidden())  // Forbidden because user can't update other users' profiles
                .andExpect(view().name("access-denied"));

        // Verify service method was not called
        verify(userService, times(0)).editUserDetails(any(), any());
    }


}