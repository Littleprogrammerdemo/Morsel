package app.unit_tests.challenge;

import app.challenges.model.Challenge;
import app.challenges.repository.ChallengeRepository;
import app.challenges.service.ChallengeService;
import app.user.model.User;
import app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChallengeServiceUnitTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChallengeService challengeService;

    private UUID userId;
    private UUID challengeId;
    private User mockUser;
    private Challenge mockChallenge;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        mockUser = new User();  // Create a mock User object
        mockUser.setId(userId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        mockChallenge = new Challenge();
        mockChallenge.setId(challengeId);
        mockChallenge.setTitle("New Challenge");
        mockChallenge.setDescription("Challenge Description");
        mockChallenge.setStartDate(LocalDate.now());
        mockChallenge.setEndDate(LocalDate.now().plusDays(7));
    }

    @Test
    void givenValidData_whenCreateChallenge_thenChallengeIsCreated() {
        // Given
        UUID userId = UUID.randomUUID();  // Mock User ID
        String title = "New Challenge";
        String description = "Challenge Description";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);

        // Create a mock User with the expected properties
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setProfilePicture("http://image.com");

        // Mock the userService to return the mockUser when called with userId
        when(userService.getByUserId(userId)).thenReturn(mockUser);

        // Create the challenge mock with expected values
        Challenge mockChallenge = Challenge.builder()
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .creator(mockUser)
                .build();

        // Mock the challengeRepository.save method to return the mockChallenge
        when(challengeRepository.save(any(Challenge.class))).thenReturn(mockChallenge);

        // When: Call the method under test
        Challenge createdChallenge = challengeService.createChallenge(userId, title, description, startDate, endDate);

        // Then: Verify the challenge is created correctly
        assertNotNull(createdChallenge);
        assertEquals(title, createdChallenge.getTitle());
        assertEquals(description, createdChallenge.getDescription());
        assertEquals(mockUser, createdChallenge.getCreator());
        verify(challengeRepository, times(1)).save(any(Challenge.class));
    }


    @Test
    void givenValidChallengeId_whenJoinChallenge_thenUserIsAddedAsParticipant() {
        // Given
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(mockChallenge));
        when(userService.getByUserId(userId)).thenReturn(mockUser);

        // When
        challengeService.joinChallenge(challengeId, userId);

        // Then
        assertTrue(mockChallenge.getParticipants().contains(mockUser));  // Assuming there's a method to get participants
        verify(challengeRepository, times(1)).save(mockChallenge);
    }

    @Test
    void givenInvalidChallengeId_whenJoinChallenge_thenThrowException() {
        // Given
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> challengeService.joinChallenge(challengeId, userId));
        assertEquals("Challenge not found", exception.getMessage());
    }

    @Test
    void givenChallengeId_whenDeleteChallenge_thenChallengeIsDeleted() {
        // Given
        doNothing().when(challengeRepository).deleteById(challengeId);

        // When
        challengeService.deleteChallenge(challengeId);

        // Then
        verify(challengeRepository, times(1)).deleteById(challengeId);
    }

    @Test
    void givenChallengeId_whenGetChallengeById_thenChallengeIsReturned() {
        // Given
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(mockChallenge));

        // When
        Optional<Challenge> foundChallenge = challengeService.getChallengeById(challengeId);

        // Then
        assertTrue(foundChallenge.isPresent());
        assertEquals(mockChallenge, foundChallenge.get());
    }

    @Test
    void givenNoChallenges_whenGetAllChallenges_thenReturnEmptyList() {
        // Given
        when(challengeRepository.findAll()).thenReturn(List.of());

        // When
        List<Challenge> challenges = challengeService.getAllChallenges();

        // Then
        assertTrue(challenges.isEmpty());
    }
}
