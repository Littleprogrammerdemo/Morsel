package app.notification.client.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class RatingRequest {


        private UUID userId; // User who is being notified
        private UUID postId; // Post that was rated
        private UUID raterId;  // User who gave the rating
        private int ratingValue; // Rating value (e.g., 1-5)

    }