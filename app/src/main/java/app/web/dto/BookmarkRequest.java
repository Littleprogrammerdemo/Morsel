package app.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Post ID cannot be null")
    private UUID postId;
}
