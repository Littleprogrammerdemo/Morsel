package app.unit_tests.messages;
import app.message.model.Message;
import app.message.repository.MessageRepository;
import app.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceUnitTest {

        @Mock
        private MessageRepository messageRepository;

        @InjectMocks
        private MessageService messageService;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void saveMessage_savesSuccessfully() {
            // Given
            Message message = new Message();
            message.setId(UUID.randomUUID());
            message.setContent("Hello, world!");

            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message savedMessage = invocation.getArgument(0);
                savedMessage.setTimestamp(LocalDateTime.now());
                return savedMessage;
            });

            // When
            Message savedMessage = messageService.saveMessage(message);

            // Then
            assertNotNull(savedMessage.getTimestamp());
            verify(messageRepository, times(1)).save(message);
        }

        @Test
        void getChatHistory_returnsSortedMessages() {
            // Given
            Message message1 = new Message(UUID.randomUUID(), "First message", LocalDateTime.now().minusMinutes(10));
            Message message2 = new Message(UUID.randomUUID(), "Second message", LocalDateTime.now());

            when(messageRepository.findAllByOrderByTimestampAsc()).thenReturn(List.of(message1, message2));

            // When
            List<Message> messages = messageService.getChatHistory();

            // Then
            assertEquals(2, messages.size());
            assertEquals("First message", messages.get(0).getContent());
            assertEquals("Second message", messages.get(1).getContent());
            verify(messageRepository, times(1)).findAllByOrderByTimestampAsc();
        }
    }