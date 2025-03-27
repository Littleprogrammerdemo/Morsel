package app.challenges.repository;

import app.challenges.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByCreatorId(UUID creatorId);
}
