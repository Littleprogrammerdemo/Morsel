package app.integration_tests;

import app.message.model.Message;
import app.message.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class MessageServiceIntegrationTest {

    @Autowired
    private MessageService messageService;


    @Test
    void saveMessage_savesSuccessfully() {
        // Given
        Message message = new Message();
        message.setContent("Integration Test Message");
        message.setTimestamp(LocalDateTime.now());

        // When
        Message savedMessage = messageService.saveMessage(message);

        // Then
        assertNotNull(savedMessage.getId());
        assertEquals("Integration Test Message", savedMessage.getContent());
        assertNotNull(savedMessage.getTimestamp());
    }
}
