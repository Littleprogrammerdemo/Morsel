package app.web.controller;

import app.message.model.Message;
import app.message.repository.MessageRepository;
import app.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    // Constructor injection to initialize dependencies
    @Autowired
    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    // Handles chat page rendering, displaying messages
    @GetMapping("/chat")
    public String getChatPage(Model model) {
        List<Message> messages = messageRepository.findAllByOrderByTimestampAsc();
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", "YourUsername"); // You can replace this with actual logic for the current user
        return "chat"; // Returns Thymeleaf template for the chat page
    }

    // WebSocket mapping for sending a message
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return messageService.saveMessage(message); // Save message and send to the topic
    }

    // Retrieves chat history
    @GetMapping("/chat/history")
    public List<Message> getChatHistory() {
        return messageService.getChatHistory(); // Fetches history from the service
    }

}
