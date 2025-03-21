package app.notification.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FriendRequest {
    private UUID senderId;
    private UUID receiverId;
}