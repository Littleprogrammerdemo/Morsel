package app.message.service;

import app.follow.repository.FollowRepository;
import app.web.dto.MessageRequest;
import  app.message.model.Message;
import  app.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Get messages received by user (Inbox)
    public List<Message> getInbox(String receiver) {
        return messageRepository.findByReceiver(receiver);
    }

    // Get chat history between two users
    public List<Message> getChatHistory(String user1, String user2) {
        return messageRepository.findChatHistory(user1, user2);
    }

    // Get list of people user has chatted with
    public List<String> getChatPartners(String user) {
        return messageRepository.findChatPartners(user);
    }

    // Send a new message
    public Message sendMessage(MessageRequest messageRequest) {
        Message message = new Message(
                messageRequest.getSender(),
                messageRequest.getReceiver(),
                messageRequest.getContent()
        );
        return messageRepository.save(message);
    }
}
