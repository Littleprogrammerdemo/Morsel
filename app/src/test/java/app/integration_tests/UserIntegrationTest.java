package app.integration_tests;

import app.exception.UsernameAlreadyExistException;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_happyPath() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("TestUser")
                .password("password")
                .build();

        // When
        User registeredUser = userService.register(registerRequest);

        // Then
        assertNotNull(registeredUser);
        assertEquals("TestUser", registeredUser.getUsername());
        assertTrue(userRepository.findByUsername("TestUser").isPresent());
    }

    @Test
    void registerUser_duplicateUsername_throwsException() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("DuplicateUser")
                .password("password")
                .build();

        userService.register(registerRequest);

        // When & Then
        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
    }

    @Test
    void changeUserRole_updatesRole() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("RoleUser")
                .password("password")
                .build();

        User registeredUser = userService.register(registerRequest);

        // When
        userService.changeUserRole(registeredUser.getId());

        // Then
        Optional<User> updatedUser = userRepository.findById(registeredUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(UserRole.ADMIN, updatedUser.get().getRole());
    }

    @Test
    void switchUserStatus_togglesStatus() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("StatusUser")
                .password("password")
                .build();

        User registeredUser = userService.register(registerRequest);
        boolean initialStatus = registeredUser.isActive();

        // When
        userService.switchStatus(registeredUser.getId());

        // Then
        Optional<User> updatedUser = userRepository.findById(registeredUser.getId());
        assertTrue(updatedUser.isPresent());
        assertNotEquals(initialStatus, updatedUser.get().isActive());
    }

    @Test
    void editUserDetails_updatesDetails() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("EditUser")
                .password("password")
                .build();

        User registeredUser = userService.register(registerRequest);

        UserEditRequest editRequest = UserEditRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .profilePicture("profile.jpg")
                .build();

        // When
        userService.editUserDetails(registeredUser.getId(), editRequest);

        // Then
        Optional<User> updatedUser = userRepository.findById(registeredUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("John", updatedUser.get().getFirstName());
        assertEquals("Doe", updatedUser.get().getLastName());
        assertEquals("johndoe@example.com", updatedUser.get().getEmail());
        assertEquals("profile.jpg", updatedUser.get().getProfilePicture());
    }
}