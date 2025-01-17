package app.user.service;

import app.exception.DomainException;
import app.like.service.LikeService;
import app.post.service.PostService;
import app.rating.service.RatingService;
import app.user.model.User;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PostService postService;
    private final LikeService likeService;
    private final RatingService ratingService;
    private final UserProperties userProperties;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       PostService postService, RatingService ratingService,
                       LikeService likeService, UserProperties userProperties) {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.postService = postService;
        this.likeService = likeService;
        this.ratingService = ratingService;
        this.userProperties = userProperties;
    }

    public User login(LoginRequest loginRequest) {

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            throw new DomainException("User with username=[%s] does not exist.".formatted(loginRequest.getUsername()), HttpStatus.BAD_REQUEST);
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new DomainException("Login attempt with incorrect password for user with id [%s].".formatted(user.getId()), HttpStatus.BAD_REQUEST);
        }

        return user;
    }

    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());
        if (userOptional.isPresent()) {
            throw new DomainException("User with username=[%s] already exist.".formatted(registerRequest.getUsername()), HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.save(initializeNewUserAccount(registerRequest));

        log.info("Successfully created new user for username [%s] with id [%s].".formatted(user.getUsername(), user.getId()));

        return userRepository.save(user);
    }

    private User initializeNewUserAccount(RegisterRequest dto) {

        return User.builder()
                .id(UUID.randomUUID())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(userProperties.getDefaultRole())
                .isActive(userProperties.isActiveByDefault())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();  // Fetching all users from the repository
    }

    public User getUserById(UUID id) {
        User user = null;
        String query = "SELECT * FROM users WHERE id = ?"; // Adjust table and column names as needed

        Connection connection = null;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, id); // Setting the UUID as a parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Assuming User has a constructor that takes these values
                    user = new User(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            rs.getString("email")
                            // Add other fields as necessary
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or handle exception as appropriate
        }

        return user;
    }

    public String getUserByUsername(String name) {
        return name;
    }

    public User getUserById(UUID id,User user) {
        return user;
    }
}