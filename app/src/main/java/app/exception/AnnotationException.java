package app.exception;

import org.hibernate.MappingException;

public class AnnotationException extends MappingException {
    public AnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationException(String message) {
        super(message);
    }
}
