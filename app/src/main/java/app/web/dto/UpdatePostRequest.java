package app.web.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdatePostRequest {

    private String title;
    private String content;
    private String categoryType;
    private MultipartFile imageFile;

    // Constructor fo tests
    public UpdatePostRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.categoryType = category;
    }

}