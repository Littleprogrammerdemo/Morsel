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
public class FollowRequest {

    @NotNull(message = "Follower ID cannot be null")
    private UUID followerId;

    @NotNull(message = "Followed ID cannot be null")
    private UUID followedId;
}
