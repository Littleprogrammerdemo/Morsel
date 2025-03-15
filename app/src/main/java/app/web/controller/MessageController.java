package app.web.controller;

import app.message.model.Message;
import app.message.service.MessageService;
import app.user.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/history")
    public ModelAndView getMessageHistory(@AuthenticationPrincipal User authenticatedUser,
                                          @RequestParam(required = false) UUID chatPartnerId) {
        if (chatPartnerId == null) {
            return new ModelAndView("error").addObject("message", "Chat partner ID is required.");
        }

        List<Message> messages = messageService.getChatHistory(authenticatedUser.getId(), chatPartnerId);

        ModelAndView modelAndView = new ModelAndView("messages");
        modelAndView.addObject("messages", messages);
        modelAndView.addObject("user", authenticatedUser);
        return modelAndView;
    }

    @PostMapping("/send")
    public String sendMessage(@AuthenticationPrincipal User authenticatedUser,
                              @RequestParam UUID receiverId,
                              @RequestParam String content) {
        messageService.sendMessage(authenticatedUser.getId(), receiverId, content);
        return "redirect:/messages/history?chatPartnerId=" + receiverId;
    }
}
