package app.web.controller;

import app.post.service.PostService;
import app.post.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class HomeController {

    private final PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        log.debug("Displaying home page");

        // Fetch latest recipes
        List<Post> recipes = postService.getAllPosts();
        model.addAttribute("recipes", recipes);

        return "home"; // Thymeleaf template name
    }
}
