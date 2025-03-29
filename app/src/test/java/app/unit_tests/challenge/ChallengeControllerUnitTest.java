package app.unit_tests.challenge;


import app.challenges.model.Challenge;
import app.challenges.service.ChallengeService;
import app.security.AuthenticationMetadata;
import app.web.controller.ChallengeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChallengeControllerUnitTest {

    @InjectMocks
    private ChallengeController challengeController;

    @Mock
    private ChallengeService challengeService;

    @Mock
    private Model model;

    private UUID challengeId;
    private AuthenticationMetadata auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        challengeId = UUID.randomUUID();
        auth = mock(AuthenticationMetadata.class);
        when(auth.getUserId()).thenReturn(UUID.randomUUID());
    }

    @Test
    void shouldGetChallengesSuccessfully() {
        // Mock data
        List<Challenge> challenges = List.of(new Challenge());
        when(challengeService.getAllChallenges()).thenReturn(challenges);

        // Execute
        String viewName = challengeController.getChallenges(model);

        // Assertions
        assertEquals("challenges", viewName);
        verify(model).addAttribute("challenges", challenges);
        verify(challengeService).getAllChallenges();
    }

    @Test
    void shouldCreateChallengeSuccessfully() {
        // Mock request parameters
        String title = "New Challenge";
        String description = "Challenge Description";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(10);

        // Execute
        String viewName = challengeController.createChallenge(auth, title, description, startDate, endDate);

        // Assertions
        assertEquals("redirect:/challenges", viewName);
        verify(challengeService).createChallenge(auth.getUserId(), title, description, startDate, endDate);
    }

    @Test
    void shouldJoinChallengeSuccessfully() {
        // Execute
        String viewName = challengeController.joinChallenge(auth, challengeId);

        // Assertions
        assertEquals("redirect:/challenges", viewName);
        verify(challengeService).joinChallenge(challengeId, auth.getUserId());
    }

    @Test
    void shouldDeleteChallengeSuccessfully() {
        // Execute
        String viewName = challengeController.deleteChallenge(challengeId);

        // Assertions
        assertEquals("redirect:/challenges", viewName);
        verify(challengeService).deleteChallenge(challengeId);
    }
}
