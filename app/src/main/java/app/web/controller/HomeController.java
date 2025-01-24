package app.web.controller;

import app.post.service.PostService;
import app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    private final PostService postService;
    private final UserService userService;

    public HomeController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        log.debug("Displaying home page");

        // Fetch latest posts to display on the home page
        model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("users", userService.getAllUsers());

        return "home";  // View name for the home page
    }

}
