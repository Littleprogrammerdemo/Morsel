package app.scheduler;

import app.user.model.User;
import app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class InactiveUsersCleaner {

    private final UserRepository userRepository;

    @Autowired
    public InactiveUsersCleaner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // For testing - fixedRate = 10000
    @Scheduled(cron = "0 0 3 1 * ?")  // Runs on the 1st of every month at 3 AM
    public void deleteInactiveUsers() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<User> inactiveUsers = userRepository.findByLastLoginBefore(oneYearAgo);

        if (inactiveUsers.isEmpty()) {
            log.info("No inactive users found for deletion.");
            return;
        }

        inactiveUsers.forEach(user -> {
            userRepository.delete(user);
            log.info("Deleted inactive user: " + user.getUsername());
        });
    }
}
