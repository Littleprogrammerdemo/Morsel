package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotNull(message = "Sender cannot be null")
    private String sender;

    @NotNull(message = "Receiver cannot be null")
    private String receiver;

    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 1000, message = "Message content can't have more than 1000 characters")
    private String content;
}
