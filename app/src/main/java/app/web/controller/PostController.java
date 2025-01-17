package app.web.controller;

import app.post.service.PostService;
import app.web.dto.PostCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable UUID id, Model model) {
        log.debug("Viewing post with id: {}", id);
        model.addAttribute("post", postService.getPostById(id));
        return "post/view";
    }

    @GetMapping("/post/new")
    public String createPostForm(Model model) {
        log.debug("Creating new post");
        model.addAttribute("post", new PostCommand());  // Prepare form object
        return "post/create";
    }

    @PostMapping("/post")
    public String createOrUpdatePost(@ModelAttribute PostCommand postCommand) {
        String title = postCommand.getTitle();  // Accessing getTitle()
        String content = postCommand.getContent();  // Accessing getContent()

        // Use the postCommand data to save the post or update it
        postService.createPost(title,
                content);

        return "redirect:/home";  // Redirect to home after successful operation
    }

    @GetMapping("/post/{id}/delete")
    public String deletePost(@PathVariable UUID id) {
        log.debug("Deleting post with id: {}", id);
        postService.deletePostById(id);
        return "redirect:/home";  // Redirect to home after deletion
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ModelAndView notFoundErrorPage(Exception exception){
        log.error("Handling not Found exception");
        ModelAndView model = new ModelAndView();
        model.addObject("exception", exception);
        model.setViewName("404error");
        return model;
    }
}
