package app.user.service;

import app.exception.DomainException;
import app.user.model.User;
import app.security.AuthenticationMetadata;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import app.user.model.UserRole;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;

import app.web.dto.UserEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserProperties userProperties;
    @Autowired
    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository, UserProperties userProperties) {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userProperties = userProperties;
    }

    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> optionUser = userRepository.findByUsername(registerRequest.getUsername());
        if (optionUser.isPresent()) {
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = userRepository.save(initializeNewUserAccount(registerRequest));

        log.info("Successfully created new user for username [%s] with id [%s].".formatted(user.getUsername(), user.getId()));

        return user;
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
    public void editUserDetails(UUID userId, UserEditRequest userEditRequest) {

        User user = getByUserId(userId);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setProfilePicture(userEditRequest.getProfilePicture());


        userRepository.save(user);
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getByUserId(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));

    }
    public void switchStatus(UUID userId) {

        User user = getByUserId(userId);

        user.setUpdatedOn(LocalDateTime.now());
        user.setActive(!user.isActive());

        userRepository.save(user);
    }

    public void changeUserRole(UUID userId) {

        User user = getByUserId(userId);

        user.setUpdatedOn(LocalDateTime.now());
        if (user.getRole() == UserRole.ADMIN){
            user.setRole(UserRole.USER);
        } else {
            user.setRole(UserRole.ADMIN);
        }

        userRepository.save(user);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));
        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }
}