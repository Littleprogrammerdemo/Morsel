package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class    UserEditRequest {

    @Size(max = 15, message = "First name can't have more than 15 symbols")
    private String firstName;

    @Size(max = 15, message = "Last name can't have more than 15 symbols")
    private String lastName;

    @Email(message = "Requires correct email format")
    private String email;

    @URL(message = "Requires correct web link format")
    private String profilePicture;


}