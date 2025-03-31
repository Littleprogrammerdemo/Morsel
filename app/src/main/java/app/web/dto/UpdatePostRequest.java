package app.web.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdatePostRequest {
    private UUID id;
    private String title;
    private String content;
    private String categoryType;
    private String imageFile;



}