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

import java.util.List;
import java.util.Optional;
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
    void givenAdminUser_whenGetAllUsers_thenReturnModelAndView() {
        // Given
        List<User> users = List.of(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        // When
        ModelAndView modelAndView = userController.getAllUsers(mock(AuthenticationMetadata.class));

        // Then
        assertEquals("admin-reports", modelAndView.getViewName());
        assertEquals(users, modelAndView.getModel().get("users"));
    }

    @Test
    void givenAuthenticatedUser_whenGetProfileMenu_thenReturnProfileMenu() {
        // Given
        UUID userId = UUID.randomUUID();
        when(authMetadata.getUserId()).thenReturn(userId); // Mock method properly

        User user = new User();
        user.setId(userId);
        when(userService.getByUserId(userId)).thenReturn(user);
        when(DtoMapper.mapUserToUserEditRequest(user)).thenReturn(new UserEditRequest());

        // When
        ModelAndView modelAndView = userController.getProfileMenu(authMetadata);

        // Then
        assertEquals("profile-menu", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
        assertNotNull(modelAndView.getModel().get("userEditRequest"));
    }




    @Test
    void givenUserId_whenSwitchUserStatus_thenRedirectToUsers() {
        // Given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).switchStatus(userId);

        // When
        String viewName = userController.switchUserStatus(userId);

        // Then
        assertEquals("redirect:/users", viewName);
    }

    @Test
    void givenUserId_whenSwitchUserRole_thenRedirectToUsers() {
        // Given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).changeUserRole(userId);

        // When
        String viewName = userController.switchUserRole(userId);

        // Then
        assertEquals("redirect:/users", viewName);
    }
}