package app.web.controller;

import app.post.model.Post;
import app.post.service.PostService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AdminSystemReport;
import app.web.dto.UserSystemReport;
import app.web.dto.mapper.DtoMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;



@Controller
@RequestMapping("/reports")
public class ReportsController {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public ReportsController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/admin")
    public ModelAndView getAdminSystemReports(HttpSession session) {
        List<User> users = userService.getAllUsers();
        List<Post> posts = postService.getAllPosts();

        AdminSystemReport adminSystemReport = DtoMapper.mapToAdminSystemReport(users, posts);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("adminSystemReports", adminSystemReport);
        modelAndView.setViewName("reports");

        return modelAndView;
    }
    @GetMapping("/user")
    public ModelAndView getUserSystemReports(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            throw new IllegalStateException("User not found in session");
        }

        User currentUser = userService.getByUserId(userId);
        if (currentUser == null) {
            throw new IllegalStateException("User does not exist");
        }

        UserSystemReport userSystemReport = DtoMapper.mapToUserSystemReport(
                currentUser,
                postService.getAllPosts(),
                userService.getAllUsers()
        );

        return new ModelAndView("user-reports", "userSystemReports", userSystemReport);
    }

}