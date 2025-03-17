package app.cloudinary;

import app.exception.CloudinaryException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadRecipeImage(MultipartFile file, String recipeId, int imageIdx) {
        // File size limit check (2MB max)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new CloudinaryException("File is too large. Maximum allowed size is 2MB.");
        }

        try {
            // Generate unique filename
            String uniqueId = UUID.randomUUID().toString();
            String publicId = "recipe-images/" + recipeId + "/" + imageIdx + "-" + uniqueId;

            Map<String, Object> options = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "recipe-images/" + recipeId,
                    "resource_type", "image",
                    "transformation", new Transformation()
                            .width(800)
                            .height(800)
                            .crop("fill")
                            .quality("auto")
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Failed to upload recipe image to Cloudinary", e);
            throw new CloudinaryException("Failed to upload recipe image");
        }
    }

    public String uploadRecipeImageFromUrl(String imageUrl, String recipeId) {
        try {
            // Generate unique filename
            String uniqueId = UUID.randomUUID().toString();
            String publicId = "generated-recipe-images/" + recipeId + "/" + uniqueId;

            Map<String, Object> options = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "generated-recipe-images/" + recipeId,
                    "resource_type", "image",
                    "transformation", new Transformation()
                            .width(800)
                            .height(800)
                            .crop("fill")
                            .quality("auto")
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(imageUrl, options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Failed to upload image from URL to Cloudinary", e);
            throw new CloudinaryException("Failed to upload generated recipe image");
        }
    }
}
