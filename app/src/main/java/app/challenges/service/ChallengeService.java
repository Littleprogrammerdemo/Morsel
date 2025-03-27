package app.challenges.service;

import app.challenges.model.Challenge;
import app.challenges.repository.ChallengeRepository;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserService userService;

    public ChallengeService(ChallengeRepository challengeRepository, UserService userService) {
        this.challengeRepository = challengeRepository;
        this.userService = userService;
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Optional<Challenge> getChallengeById(UUID id) {
        return challengeRepository.findById(id);
    }
@Transactional
    public Challenge createChallenge(UUID creatorId, String title, String description, LocalDate startDate, LocalDate endDate) {
        User creator = userService.getByUserId(creatorId);
        Challenge challenge = Challenge.builder()
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .creator(creator)
                .build();
        return challengeRepository.save(challenge);
    }
    @Transactional
    public void joinChallenge(UUID challengeId, UUID userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        User user = userService.getByUserId(userId);
        challenge.addParticipant(user);
        challengeRepository.save(challenge);
    }

    public void deleteChallenge(UUID challengeId) {
        challengeRepository.deleteById(challengeId);
    }
}
