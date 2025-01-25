package app.web.controller;

import app.user.model.User;
import app.user.service.UserService;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ModelAndView getUserDetailsPage(@PathVariable UUID id) {

        User user = userService.getUserById(id);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", user);
        modelAndView.setViewName("user-details");

        return modelAndView;
    }

    @GetMapping
    public ModelAndView getUsersPage() {

        List<User> allUsers = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("users", allUsers);
        modelAndView.setViewName("users");

        return modelAndView;
    }
    private User getCurrentUser() {
        // Replace this with actual logic to retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return (User) authentication.getPrincipal(); // Assumes the principal is of type User
    }
}