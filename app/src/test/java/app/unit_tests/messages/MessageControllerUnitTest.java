package app.unit_tests.messages;

import app.message.model.Message;
import app.message.repository.MessageRepository;
import app.message.service.MessageService;
import app.web.controller.MessageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageControllerUnitTest {

    @InjectMocks
    private MessageController messageController;

    @Mock
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private Model model;

    private Message testMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a test message
        testMessage = new Message();
        testMessage.setId(UUID.randomUUID());
        testMessage.setContent("Hello, world!");
        testMessage.setSender("TestUser");
    }

    @Test
    void shouldRenderChatPageSuccessfully() {
        // Mock repository response
        List<Message> messages = List.of(testMessage);
        when(messageRepository.findAllByOrderByTimestampAsc()).thenReturn(messages);

        // Execute
        String viewName = messageController.getChatPage(model);

        // Assertions
        assertEquals("chat", viewName);
        verify(model).addAttribute("messages", messages);
        verify(model).addAttribute("currentUser", "YourUsername"); // Static username in controller
    }

    @Test
    void shouldSendMessageSuccessfully() {
        // Mock message service to return the saved message
        when(messageService.saveMessage(testMessage)).thenReturn(testMessage);

        // Execute
        Message returnedMessage = messageController.sendMessage(testMessage);

        // Assertions
        assertEquals(testMessage, returnedMessage);
        verify(messageService).saveMessage(testMessage);
    }

    @Test
    void shouldReturnChatHistorySuccessfully() {
        // Mock service response
        List<Message> chatHistory = List.of(testMessage);
        when(messageService.getChatHistory()).thenReturn(chatHistory);

        // Execute
        List<Message> result = messageController.getChatHistory();

        // Assertions
        assertEquals(chatHistory, result);
        verify(messageService).getChatHistory();
    }
}
