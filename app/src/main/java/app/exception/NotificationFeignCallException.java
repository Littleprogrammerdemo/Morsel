package app.exception;

public class NotificationFeignCallException extends RuntimeException {

    public NotificationFeignCallException(String message) {
        super(message);
    }
}