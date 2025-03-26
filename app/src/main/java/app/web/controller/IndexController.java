package app.web.controller;

import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import app.security.AuthenticationMetadata;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public IndexController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());
        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getByUserId(authenticationMetadata.getUserId());
        List<Post> posts = postService.getAllPosts(); // Fetch all posts

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("posts", posts); // Add posts to the model

        return modelAndView;
    }

}
