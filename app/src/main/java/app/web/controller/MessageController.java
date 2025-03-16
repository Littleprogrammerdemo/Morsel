package app.web.controller;

import app.web.dto.MessageRequest;
import app.message.model.Message;
import app.message.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 1. Get messages received by user (Inbox)
    @GetMapping("/inbox/{username}")
    public String getInbox(@PathVariable String username, Model model) {
        List<Message> messages = messageService.getInbox(username);
        model.addAttribute("messages", messages);
        model.addAttribute("username", username);
        return "inbox"; // Thymeleaf template
    }

    // 2. Get chat with a specific person
    @GetMapping("/chat/{user1}/{user2}")
    public String getChatHistory(@PathVariable String user1, @PathVariable String user2, Model model) {
        List<Message> messages = messageService.getChatHistory(user1, user2);
        model.addAttribute("messages", messages);
        model.addAttribute("user1", user1);
        model.addAttribute("user2", user2);
        return "chat"; // Thymeleaf template
    }

    // 3. Get chat history overview (all contacts user has messaged)
    @GetMapping("/history/{username}")
    public String getChatPartners(@PathVariable String username, Model model) {
        List<String> chatPartners = messageService.getChatPartners(username);
        model.addAttribute("chatPartners", chatPartners);
        model.addAttribute("username", username);
        return "chat-history"; // Thymeleaf template
    }

    // 4. Send a new message (Using DTO with Validation)
    @PostMapping("/send")
    public String sendMessage(@ModelAttribute @Valid MessageRequest messageRequest) {
        messageService.sendMessage(messageRequest);
        return "redirect:/messages/chat/" + messageRequest.getSender() + "/" + messageRequest.getReceiver();
    }
    @GetMapping("/send")
    public String showSendMessageForm(Model model) {
        model.addAttribute("messageRequest", new MessageRequest());
        return "send_message_form"; // Thymeleaf template for sending messages
    }

}
