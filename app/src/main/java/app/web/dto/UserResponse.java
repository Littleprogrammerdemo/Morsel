package app.web.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponse {
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;


}

