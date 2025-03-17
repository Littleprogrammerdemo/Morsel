package app.exception;

public class CloudinaryException extends RuntimeException {
    public CloudinaryException(String message) {
        super(message);
    }
}