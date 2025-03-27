package app.web.controller;

import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
import app.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    public ModelAndView updateUserProfile(@PathVariable UUID id,
                                          @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                          @Valid UserEditRequest userEditRequest,
                                          BindingResult bindingResult) {
        // Ensure users can only edit their own profile
        if (!id.equals(authenticationMetadata.getUserId())) {
            throw new AccessDeniedException("You can only edit your own profile.");
        }

        if (bindingResult.hasErrors()) {
            User user = userService.getByUserId(id);
            ModelAndView modelAndView = new ModelAndView("profile-menu");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);
            return modelAndView;
        }

        userService.editUserDetails(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/admin-panel";
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String switchUserRole(@PathVariable UUID id) {

        userService.changeUserRole(id);

        return "redirect:/admin-panel";
    }
}