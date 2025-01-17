package app.user.service;

import app.user.model.User;
import app.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Конструктор с инжектиране на зависимостта UserRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        // Ако потребителят не съществува, хвърляме изключение
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Връщаме потребителя в формат, който Spring Security може да използва
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.stream().toString())
                .password(String.valueOf(user.getClass()))
                .roles(String.valueOf(user.getClass().isArray()))
                .build();
    }
}