package app.web.dto;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileForFollow {
    private UUID id;
    private String username;
    private String bio;
}
