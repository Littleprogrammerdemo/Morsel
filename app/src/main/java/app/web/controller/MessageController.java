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
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Display the form to send a new message
    @GetMapping("/send")
    public String showSendMessageForm(Model model) {
        model.addAttribute("messageRequest", new MessageRequest());
        return "send_message_form";
    }

    // Handle form submission for sending a new message
    @PostMapping("/send")
    public String sendMessage(@Valid @ModelAttribute("messageRequest") MessageRequest messageRequest, Model model) {

        messageService.sendMessage(messageRequest);

        // Add a success message to the model
        model.addAttribute("message", "Message sent successfully!");

        return "redirect:/home";
    }

}
