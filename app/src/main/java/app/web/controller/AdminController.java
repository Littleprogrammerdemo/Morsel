package app.web.controller;

import app.admin.service.AdminService;
import app.post.service.PostService;
import app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Slf4j
@Controller
public class AdminController {

    private final AdminService adminService;
    private final PostService postService;
    private final UserService userService;

    public AdminController(AdminService adminService, PostService postService, UserService userService) {
        this.adminService = adminService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {
        log.debug("Admin Dashboard");

        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allPosts", postService.getAllPosts());

        return "admin/dashboard";  // Admin dashboard view
    }

    @GetMapping("/admin/user/{userId}/delete")
    public String deleteUser(@PathVariable UUID userId) {
        log.debug("Deleting user with id: {}", userId);
        adminService.deleteUserById(userId);
        return "redirect:/admin";  // Redirect to admin dashboard after deletion
    }

    @GetMapping("/admin/post/{postId}/delete")
    public String deletePost(@PathVariable UUID postId) {
        log.debug("Deleting post with id: {}", postId);
        postService.deletePostById(postId);
        return "redirect:/admin";  // Redirect to admin dashboard after deletion
    }
}
