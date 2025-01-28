package app.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PostCommand {
    @NotEmpty(message = "Title cannot be empty")
    @Size(min = 5, message = "Title must be at least 5 characters long")
    private String title;

    @NotEmpty(message = "Content cannot be empty")
    @Size(min = 20, message = "Content must be at least 20 characters long")
    private String content;
}
