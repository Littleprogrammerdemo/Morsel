package app.web.controller;

import app.challenges.model.Challenge;
import app.challenges.service.ChallengeService;
import app.security.AuthenticationMetadata;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/challenges")
public class ChallengeController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public String getChallenges(Model model) {
        List<Challenge> challenges = challengeService.getAllChallenges();
        model.addAttribute("challenges", challenges);
        return "challenges";
    }

    @PostMapping("/create")
    public String createChallenge(
            @AuthenticationPrincipal AuthenticationMetadata auth,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        challengeService.createChallenge(auth.getUserId(), title, description, startDate, endDate);
        return "redirect:/challenges";
    }

    @PostMapping("/join/{id}")
    public String joinChallenge(@AuthenticationPrincipal AuthenticationMetadata auth, @PathVariable UUID id) {
        challengeService.joinChallenge(id, auth.getUserId());
        return "redirect:/challenges";
    }

    @PostMapping("/delete/{id}")
    public String deleteChallenge(@PathVariable UUID id) {
        challengeService.deleteChallenge(id);
        return "redirect:/challenges";
    }
}
