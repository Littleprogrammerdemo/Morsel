package app.web.controller;

import app.post.model.Post;
import app.post.service.PostService;
import app.security.RequireAdminRole;
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

import static app.security.SessionInterceptor.USER_ID_SESSION_ATTRIBUTE;


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

    @GetMapping
    @RequireAdminRole
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
        UUID userId = (UUID) session.getAttribute(USER_ID_SESSION_ATTRIBUTE);
        User currentUser = userService.getByUserId(userId);
        List<Post> allPosts = postService.getAllPosts();
        List<User> allUsers = userService.getAllUsers();

        UserSystemReport userSystemReport = DtoMapper.mapToUserSystemReport(currentUser, allPosts, allUsers);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userSystemReports", userSystemReport);
        modelAndView.setViewName("user-reports");

        return modelAndView;
    }
}