package app.message.service;

import app.message.model.Message;
import app.message.model.MessageStatus;
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

            // Create and save the message
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setStatus(MessageStatus.valueOf("UNREAD"));  // New messages are initially marked as "UNREAD"
            messageRepository.save(message);

            // Send the message to the receiver in real-time via WebSocket
            messagingTemplate.convertAndSendToUser(receiver.getId().toString(), "/queue/messages", message);

            // Optionally, notify the sender that the message was successfully sent
            return message;
        }

        public List<Message> getChatHistory(UUID senderId, UUID receiverId) {
            // Fetch the chat history and mark unread messages as read when fetched
            List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrderByTimestamp(senderId, receiverId);
            for (Message message : messages) {
                if ("UNREAD".equals(message.getStatus())) {
                    message.setStatus(MessageStatus.valueOf("READ"));  // Mark message as read when fetching
                    messageRepository.save(message);  // Save the updated status
                }
            }
            return messages;
        }
    }

