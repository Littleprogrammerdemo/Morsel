package app.web.controller;

import app.message.model.Message;
import app.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestParam UUID senderId,
                               @RequestParam UUID receiverId,
                               @RequestParam String content) {
        return messageService.sendMessage(senderId, receiverId, content);
    }

    @GetMapping("/history")
    public List<Message> getChatHistory(@RequestParam UUID senderId,
                                        @RequestParam UUID receiverId) {
        return messageService.getChatHistory(senderId, receiverId);
    }
}
