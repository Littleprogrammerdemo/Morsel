package app.web;

import app.exception.NotificationFeignCallException;
import app.exception.UsernameAlreadyExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ExceptionAdvice {


    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, UsernameAlreadyExistException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }

    @ExceptionHandler(NotificationFeignCallException.class)
    public String handleNotificationFeignCallException(RedirectAttributes redirectAttributes, NotificationFeignCallException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("clearHistoryErrorMessage", message);
        return "redirect:/notifications";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
            MissingRequestValueException.class //When a required request parameter is missing.
    )
    public ModelAndView handleNotFoundExceptions(Exception exception) {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class) // When there is a type mismatch (invalid input type)
    public ModelAndView handleTypeMismatchException(TypeMismatchException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bad-request");
        modelAndView.addObject("errorMessage", "Invalid input type.");
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class) // When access to a resource is denied
    public ModelAndView handleAccessDeniedException(AccessDeniedException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("access-denied");
        modelAndView.addObject("errorMessage", "You do not have permission to access this resource.");
        return modelAndView;
    }
}