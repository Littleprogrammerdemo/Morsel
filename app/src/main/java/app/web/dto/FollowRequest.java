    package app.web.dto;

    import app.user.model.User;
    import jakarta.validation.constraints.NotNull;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;
    import java.util.UUID;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class FollowRequest {

        @NotNull
        private UUID followerId;

        @NotNull
        private UUID followedId;
        private List<User> followers;  // List of users who follow the given user
        private List<User> followedUsers;  // List of users the given user is following
    }
