package app.web.controller;


import app.like.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/post/{postId}/like")
    public String likePost(@PathVariable UUID postId) {
        log.debug("Liking post with id: {}", postId);
        likeService.likePost(postId);
        return "redirect:/post/" + postId;  // Redirect back to the post
    }

    @PostMapping("/post/{postId}/unlike")
    public String unlikePost(@PathVariable UUID postId) {
        log.debug("Unliking post with id: {}", postId);
        likeService.unlikePost(postId);
        return "redirect:/post/" + postId;  // Redirect back to the post
    }
}
