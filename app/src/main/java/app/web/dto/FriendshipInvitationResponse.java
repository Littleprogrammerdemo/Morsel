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
    public class FriendshipInvitationResponse {
        private UUID id;
        private UUID invitingUserId;
        private String invitingUsername;
        private UUID acceptingUserId;
        private String acceptingUsername;
        private boolean accepted;

    }
