package app.integration_tests;

import app.challenges.model.Challenge;
import app.challenges.repository.ChallengeRepository;
import app.challenges.service.ChallengeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ChallengeServiceIntegrationTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private ChallengeRepository challengeRepository;


    @Test
    void givenNoChallenges_whenGetAllChallenges_thenReturnEmptyList() {
        // Given
        challengeRepository.deleteAll();

        // When
        List<Challenge> challenges = challengeService.getAllChallenges();

        // Then
        assertTrue(challenges.isEmpty());
    }

}
