package app.message.repository;

import app.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    // Inbox: Messages sent to you
    List<Message> findByReceiver(String receiver);

    // Chat with specific person (messages between sender and receiver)
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(String user1, String user2);

    // Get list of people you have chatted with
    @Query("SELECT DISTINCT CASE WHEN m.sender = :user THEN m.receiver ELSE m.sender END FROM Message m WHERE m.sender = :user OR m.receiver = :user")
    List<String> findChatPartners(String user);
}
