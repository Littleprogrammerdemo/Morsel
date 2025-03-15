package app.web.controller;

import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
import app.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public UserController(UserService userService,PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin-reports");
        modelAndView.addObject("users", users);

        return modelAndView;
    }
    @GetMapping("/{id}/profile")
    public ModelAndView getProfileMenu(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", DtoMapper.mapUserToUserEditRequest(user));

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id, @Valid UserEditRequest userEditRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            User user = userService.getByUserId(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile-menu");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);
            return modelAndView;
        }

        userService.editUserDetails(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }
    @PutMapping("/{id}/status")
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/users";
    }

    @PutMapping("/{id}/role")
    public String switchUserRole(@PathVariable UUID id) {

        userService.changeUserRole(id);

        return "redirect:/users";
    }
}