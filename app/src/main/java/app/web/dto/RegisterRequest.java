package app.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Pattern(regexp = "\\d{8}", message = "Password must be exactly 8 digits")
    private String password;

}
