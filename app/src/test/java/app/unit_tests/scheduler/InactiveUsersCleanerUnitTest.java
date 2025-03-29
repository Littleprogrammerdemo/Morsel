package app.unit_tests.scheduler;

import app.scheduler.InactiveUsersCleaner;
import app.user.model.User;
import app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InactiveUsersCleanerUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InactiveUsersCleaner inactiveUsersCleaner;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setId(UUID.randomUUID());
        activeUser.setUsername("activeUser");
        activeUser.setLastLogin(LocalDateTime.now().minusDays(10));

        inactiveUser = new User();
        inactiveUser.setId(UUID.randomUUID());
        inactiveUser.setUsername("inactiveUser");
        inactiveUser.setLastLogin(LocalDateTime.now().minusYears(2));
    }

    @Test
    void shouldDeleteInactiveUsers() {
        // Simulate inactive user being fetched
        when(userRepository.findByLastLoginBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(inactiveUser));

        inactiveUsersCleaner.deleteInactiveUsers();

        verify(userRepository, times(1)).delete(inactiveUser);
    }

    @Test
    void shouldLogNoInactiveUsersWhenListIsEmpty() {
        // Simulate no inactive users
        when(userRepository.findByLastLoginBefore(any(LocalDateTime.class)))
                .thenReturn(List.of());

        inactiveUsersCleaner.deleteInactiveUsers();

        verify(userRepository, never()).delete(any(User.class));
    }
}
