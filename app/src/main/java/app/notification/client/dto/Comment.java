package app.notification.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Comment {
    private UUID userId;
    private UUID postId;
    private String content;
}