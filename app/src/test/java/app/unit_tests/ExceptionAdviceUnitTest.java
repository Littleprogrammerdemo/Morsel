package app.unit_tests;

import app.exception.NotificationFeignCallException;
import app.exception.UsernameAlreadyExistException;
import app.web.ExceptionAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.TypeMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionAdviceUnitTest {

    @InjectMocks
    private ExceptionAdvice exceptionAdvice;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ModelAndView modelAndView;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRedirectToRegisterPageWhenUsernameAlreadyExists() {
        // Arrange
        String errorMessage = "Username already exists";
        UsernameAlreadyExistException exception = new UsernameAlreadyExistException(errorMessage);

        // Act
        String result = exceptionAdvice.handleUsernameAlreadyExist(request, redirectAttributes, exception);

        // Assert
        assertEquals("redirect:/register", result);
        verify(redirectAttributes).addFlashAttribute("usernameAlreadyExistMessage", errorMessage);
    }

    @Test
    void shouldRedirectToNotificationsPageWhenNotificationFeignCallExceptionOccurs() {
        // Arrange
        String errorMessage = "Notification service error";
        NotificationFeignCallException exception = new NotificationFeignCallException(errorMessage);

        // Act
        String result = exceptionAdvice.handleNotificationFeignCallException(redirectAttributes, exception);

        // Assert
        assertEquals("redirect:/notifications", result);
        verify(redirectAttributes).addFlashAttribute("clearHistoryErrorMessage", errorMessage);
    }

    @Test
    void shouldReturnNotFoundPageForMissingRequestValueException() {
        // Arrange
        MissingRequestValueException exception = new MissingRequestValueException("missingParam");

        // Act
        ModelAndView result = exceptionAdvice.handleNotFoundExceptions(exception);

        // Assert
        assertEquals("not-found", result.getViewName());
    }

    @Test
    void shouldReturnNotFoundPageForNoResourceFoundException() {
        // Arrange
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/some-resource");

        // Act
        ModelAndView result = exceptionAdvice.handleNotFoundExceptions(exception);

        // Assert
        assertEquals("not-found", result.getViewName());
    }

    @Test
    void shouldReturnInternalServerErrorPageForAnyException() {
        // Arrange
        Exception exception = new Exception("Internal server error");

        // Act
        ModelAndView result = exceptionAdvice.handleAnyException(exception);

        // Assert
        assertEquals("internal-server-error", result.getViewName());
        assertEquals("Exception", result.getModel().get("errorMessage"));
    }

    @Test
    void shouldReturnAccessDeniedPageForAccessDeniedException() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ModelAndView result = exceptionAdvice.handleAccessDeniedException(exception);

        // Assert
        assertEquals("access-denied", result.getViewName());
        assertEquals("You do not have permission to access this resource.", result.getModel().get("errorMessage"));
    }
}
