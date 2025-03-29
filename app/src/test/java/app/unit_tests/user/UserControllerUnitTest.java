package app.unit_tests.user;

import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.UserController;
import app.web.dto.UserEditRequest;
import app.web.dto.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationMetadata authMetadata;

    @Mock
    private PostService postService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;


    @Test
    void givenUserNotOwnProfile_whenUpdateUserProfile_thenAccessDenied() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID authenticatedUserId = UUID.randomUUID();
        when(authMetadata.getUserId()).thenReturn(authenticatedUserId);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            userController.updateUserProfile(userId, authMetadata, new UserEditRequest(), bindingResult);
        });
        assertEquals("You can only edit your own profile.", exception.getMessage());
    }

    @Test
    void givenValidationErrors_whenUpdateUserProfile_thenReturnProfileMenu() {
        // Given
        UUID userId = UUID.randomUUID();
        when(authMetadata.getUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(true);

        User user = new User();
        user.setId(userId);
        when(userService.getByUserId(userId)).thenReturn(user);

        UserEditRequest userEditRequest = new UserEditRequest();
        // When
        ModelAndView modelAndView = userController.updateUserProfile(userId, authMetadata, userEditRequest, bindingResult);

        // Then
        assertEquals("profile-menu", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertEquals(userEditRequest, modelAndView.getModel().get("userEditRequest"));
    }

    @Test
    void givenValidProfile_whenUpdateUserProfile_thenRedirectToHome() {
        // Given
        UUID userId = UUID.randomUUID();
        when(authMetadata.getUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(false);

        UserEditRequest userEditRequest = new UserEditRequest();
        // When
        ModelAndView modelAndView = userController.updateUserProfile(userId, authMetadata, userEditRequest, bindingResult);

        // Then
        assertEquals("redirect:/home", modelAndView.getViewName());
        verify(userService).editUserDetails(eq(userId), eq(userEditRequest)); // Ensure service method is called
    }


}
