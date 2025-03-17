package app.message.service;

import app.web.dto.MessageRequest;
import  app.message.model.Message;
import  app.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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
