package app.message.service;

import app.message.model.Message;
import app.message.repository.MessageRepository;
import app.user.model.User;
import app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    @Autowired
    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @Transactional
    public Message sendMessage(UUID senderId, UUID receiverId, String content) {
        User sender = userService.getByUserId(senderId);
        User receiver = userService.getByUserId(receiverId);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Invalid sender or receiver.");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        messageRepository.save(message);

        // Send real-time message via WebSocket
        messagingTemplate.convertAndSendToUser(receiver.getId().toString(), "/queue/messages", message);

        return message;
    }

    public List<Message> getChatHistory(UUID senderId, UUID receiverId) {
        return messageRepository.findBySenderIdAndReceiverIdOrderByTimestamp(senderId, receiverId);
    }
}
