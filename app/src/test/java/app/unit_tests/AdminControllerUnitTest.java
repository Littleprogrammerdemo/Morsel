package app.unit_tests;

import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.controller.AdminController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerUnitTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;


    @Mock
    private AuthenticationMetadata authenticationMetadata;

    private User adminUser;
    private UUID adminUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create an admin user
        adminUserId = UUID.randomUUID();
        adminUser = new User();
        adminUser.setId(adminUserId);
        adminUser.setUsername("AdminUser");

        when(authenticationMetadata.getUserId()).thenReturn(adminUserId);
        when(userService.getByUserId(adminUserId)).thenReturn(adminUser);
    }

    @Test
    void shouldReturnAdminPanelWithUsersSuccessfully() {
        // Mock user list
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("User1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("User2");

        List<User> users = List.of(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Execute
        ModelAndView modelAndView = adminController.getAllUsers(authenticationMetadata);

        // Assertions
        assertEquals("admin-reports", modelAndView.getViewName());
        assertEquals(adminUser, modelAndView.getModel().get("user"));
        assertEquals(users, modelAndView.getModel().get("users"));
    }
}
