package app.api_tests;

import app.exception.UsernameAlreadyExistException;
import app.security.AuthenticationMetadata;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.controller.IndexController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static app.TestBuilder.createRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
class IndexApiTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnIndexView_whenAccessingRootPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void shouldDisplayRegisterView_whenOpeningRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void shouldDisplayLoginView_whenOpeningLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void shouldShowErrorMessage_whenLoginFails() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest", "errorMessage"));
    }

    @Test
    void shouldRedirectToLogin_whenRegistrationIsSuccessful() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "TestUser")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).register(any());
    }

    @Test
    void shouldRedirectBackWithError_whenUsernameExists() throws Exception {
        when(userService.register(any())).thenThrow(new UsernameAlreadyExistException("Username already taken!"));

        mockMvc.perform(post("/register")
                        .param("username", "TestUser")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("usernameAlreadyExistMessage"));

        verify(userService, times(1)).register(any());
    }

    @Test
    void shouldRenderRegisterPageAgain_whenRegistrationDataIsInvalid() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }

    @Test
    void shouldReturnHomeView_whenAuthenticatedUserAccessesHomePage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata userPrincipal = new AuthenticationMetadata(userId, "TestUser", "12345678", UserRole.USER, true);
        when(userService.getByUserId(any())).thenReturn(createRandomUser());

        mockMvc.perform(get("/home").with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getByUserId(userId);
    }

    @Test
    void shouldRedirectToLogin_whenUnauthenticatedUserAccessesHomePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection());

        verify(userService, never()).getByUserId(any());
    }
}
