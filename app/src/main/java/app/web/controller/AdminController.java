package app.web.controller;

import app.post.service.PostService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/admin-panel")
public class AdminController {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public AdminController(UserService userService,PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin-reports");
        modelAndView.addObject("user", user);
        modelAndView.addObject("users", users);

        return modelAndView;
    }

}
