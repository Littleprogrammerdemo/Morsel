package app.unit_tests.cloudinary;

import app.cloudinary.CloudinaryService;
import app.exception.CloudinaryException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceUnitTest {

    @Mock
    private Cloudinary cloudinary;  // Mock the Cloudinary instance

    @InjectMocks
    private CloudinaryService cloudinaryService;  // The service being tested

    @Mock
    private MultipartFile file;  // Mock the MultipartFile to simulate file upload

    @Test
    void givenValidFile_whenUploadRecipeImage_thenReturnImageUrl() throws IOException {
        // Given
        String recipeId = "recipe123";
        int imageIdx = 1;
        String expectedUrl = "https://cloudinary.com/secure-url";

        // Mock the MultipartFile behavior
        when(file.getSize()).thenReturn(1024L);  // File size < 2MB
        when(file.getBytes()).thenReturn("fileContent".getBytes());

        // Mock Cloudinary behavior for file upload
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", expectedUrl);
        when(cloudinary.uploader().upload(any(byte[].class), any(Map.class))).thenReturn(uploadResult);

        // When
        String uploadedUrl = cloudinaryService.uploadRecipeImage(file, recipeId, imageIdx);

        // Then
        assertEquals(expectedUrl, uploadedUrl);
        verify(cloudinary.uploader(), times(1)).upload(any(byte[].class), any(Map.class));  // Ensure uploader's upload method is called
    }

    @Test
    void givenFileTooLarge_whenUploadRecipeImage_thenThrowCloudinaryException() throws IOException {
        // Given
        String recipeId = "recipe123";
        int imageIdx = 1;

        // Mock the MultipartFile behavior for file size > 2MB
        when(file.getSize()).thenReturn(3L * 1024 * 1024);  // File size > 2MB

        // When & Then
        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadRecipeImage(file, recipeId, imageIdx));
        verify(cloudinary.uploader(), never()).upload(any(byte[].class), any(Map.class));
    }

    @Test
    void givenIOException_whenUploadRecipeImage_thenThrowCloudinaryException() throws IOException {
        // Given
        String recipeId = "recipe123";
        int imageIdx = 1;

        // Mock MultipartFile behavior
        when(file.getSize()).thenReturn(1024L);  // Valid file size
        when(file.getBytes()).thenThrow(new IOException("Mocked exception"));

        // When & Then
        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadRecipeImage(file, recipeId, imageIdx));
        verify(cloudinary.uploader(), never()).upload(any(byte[].class), any(Map.class));
    }

    @Test
    void givenValidUrl_whenUploadRecipeImageFromUrl_thenReturnImageUrl() throws IOException {
        // Given
        String imageUrl = "http://image.com";
        String recipeId = "recipe123";
        String expectedUrl = "https://cloudinary.com/secure-url";

        // Mock Cloudinary behavior for URL upload
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", expectedUrl);
        when(cloudinary.uploader().upload(any(String.class), any(Map.class))).thenReturn(uploadResult);

        // When
        String uploadedUrl = cloudinaryService.uploadRecipeImageFromUrl(imageUrl, recipeId);

        // Then
        assertEquals(expectedUrl, uploadedUrl);
        verify(cloudinary.uploader(), times(1)).upload(any(String.class), any(Map.class));
    }

    @Test
    void givenIOException_whenUploadRecipeImageFromUrl_thenThrowCloudinaryException() throws IOException {
        // Given
        String imageUrl = "http://image.com";
        String recipeId = "recipe123";

        // Mock Cloudinary behavior for URL upload
        when(cloudinary.uploader().upload(any(String.class), any(Map.class))).thenThrow(new IOException("Mocked exception"));

        // When & Then
        assertThrows(CloudinaryException.class, () -> cloudinaryService.uploadRecipeImageFromUrl(imageUrl, recipeId));
        verify(cloudinary.uploader(), times(1)).upload(any(String.class), any(Map.class));  // Ensure uploader's upload method is called
    }
}
