package app;

import app.user.model.User;
import app.user.model.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestBuilder {
    public static User createRandomUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("TestUser")
                .password("12345678")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        return user;
    }
}