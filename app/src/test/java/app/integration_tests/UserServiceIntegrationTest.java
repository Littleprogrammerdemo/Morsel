package app.integration_tests;
import app.exception.DomainException;
import app.exception.UsernameAlreadyExistException;
import app.notification.service.NotificationService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Test
    void givenNewUser_whenRegister_thenUserIsPersisted() {
        RegisterRequest request = new RegisterRequest("newUser", "password");
        User user = userService.register(request);

        assertNotNull(user.getId());
        assertEquals("newUser", user.getUsername());
    }

    @Test
    void givenExistingUser_whenRegister_thenThrowException() {
        RegisterRequest request = new RegisterRequest("existingUser", "password");
        userService.register(request);

        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(request));
    }

    @Test
    void givenUsers_whenGetAllUsers_thenReturnAllUsers() {
        userService.register(new RegisterRequest("user1", "password"));
        userService.register(new RegisterRequest("user2", "password"));

        List<User> users = userService.getAllUsers();

        assertTrue(users.size() >= 2);
    }

    @Test
    void givenUserId_whenGetByUserId_thenReturnUser() {
        User user = userService.register(new RegisterRequest("userToFind", "password"));
        User foundUser = userService.getByUserId(user.getId());

        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void givenInvalidUserId_whenGetByUserId_thenThrowException() {
        UUID invalidId = UUID.randomUUID();
        assertThrows(DomainException.class, () -> userService.getByUserId(invalidId));
    }

    @Test
    void givenUser_whenSwitchStatus_thenToggleStatus() {
        User user = userService.register(new RegisterRequest("toggleUser", "password"));
        boolean initialStatus = user.isActive();

        userService.switchStatus(user.getId());
        User updatedUser = userService.getByUserId(user.getId());

        assertNotEquals(initialStatus, updatedUser.isActive());
    }

    @Test
    void givenUser_whenChangeUserRole_thenToggleRole() {
        User user = userService.register(new RegisterRequest("roleUser", "password"));
        UserRole initialRole = user.getRole();

        userService.changeUserRole(user.getId());
        User updatedUser = userService.getByUserId(user.getId());

        assertNotEquals(initialRole, updatedUser.getRole());
    }

    @Test
    void givenValidUser_whenEditUserDetails_thenUpdateDetails() {
        User user = userService.register(new RegisterRequest("editUser", "password"));
        UserEditRequest editRequest = new UserEditRequest("John", "Doe", "john@example.com", "http://image.com");

        userService.editUserDetails(user.getId(), editRequest);
        User updatedUser = userService.getByUserId(user.getId());

        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("john@example.com", updatedUser.getEmail());
        assertEquals("http://image.com", updatedUser.getProfilePicture());
    }

    @Test
    void givenInvalidUser_whenEditUserDetails_thenThrowException() {
        UUID invalidId = UUID.randomUUID();
        UserEditRequest editRequest = new UserEditRequest("John", "Doe", "john@example.com", "http://image.com");

        assertThrows(DomainException.class, () -> userService.editUserDetails(invalidId, editRequest));
    }
}
