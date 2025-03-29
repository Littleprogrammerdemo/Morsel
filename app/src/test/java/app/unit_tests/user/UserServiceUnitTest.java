package app.unit_tests.user;

import app.exception.DomainException;
import app.exception.UsernameAlreadyExistException;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private UserService userService;

    @Test
    void givenExistingUsername_whenRegister_thenThrowException() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password");
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(new User()));

        // When & Then
        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenNewUsername_whenRegister_thenSaveUser() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password");
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userProperties.getDefaultRole()).thenReturn(UserRole.USER);
        when(userProperties.isActiveByDefault()).thenReturn(true);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(registerRequest.getUsername());
        when(userRepository.save(any())).thenReturn(user);

        // When
        User registeredUser = userService.register(registerRequest);

        // Then
        assertEquals(registerRequest.getUsername(), registeredUser.getUsername());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenExistingUsers_whenGetAllUsers_thenReturnUsers() {
        // Given
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void givenUserId_whenGetByUserId_thenReturnUser() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User foundUser = userService.getByUserId(userId);

        // Then
        assertEquals(userId, foundUser.getId());
    }

    @Test
    void givenInvalidUserId_whenGetByUserId_thenThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DomainException.class, () -> userService.getByUserId(userId));
    }

    @Test
    void givenUserId_whenSwitchStatus_thenToggleStatus() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setActive(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchStatus(userId);

        // Then
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenUserWithRoleAdmin_whenChangeUserRole_thenAssignUserRole() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.ADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.changeUserRole(userId);

        // Then
        assertEquals(UserRole.USER, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenUserWithRoleUser_whenChangeUserRole_thenAssignAdminRole() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.changeUserRole(userId);

        // Then
        assertEquals(UserRole.ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenUsername_whenLoadUserByUsername_thenReturnUserDetails() {
        // Given
        String username = "testUser";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword("password");
        user.setRole(UserRole.USER);
        user.setActive(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void givenInvalidUsername_whenLoadUserByUsername_thenThrowException() {
        // Given
        String username = "invalidUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void givenUser_whenEditUserDetails_thenUpdateUserDetails() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEditRequest editRequest = new UserEditRequest();
        editRequest.setFirstName("John");
        editRequest.setLastName("Doe");
        editRequest.setEmail("john@example.com");
        editRequest.setProfilePicture("http://image.com");

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.editUserDetails(userId, editRequest);

        // Then
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("http://image.com", user.getProfilePicture());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenInvalidUser_whenEditUserDetails_thenThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEditRequest editRequest = new UserEditRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DomainException.class, () -> userService.editUserDetails(userId, editRequest));
    }
}
