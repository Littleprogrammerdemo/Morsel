package app.message.repository;

import app.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestamp(UUID senderId, UUID receiverId);
    List<Message> findByReceiverIdAndStatus(UUID receiverId, String status);
}
