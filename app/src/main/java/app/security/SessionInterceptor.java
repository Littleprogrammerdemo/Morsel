package app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    public static final String USER_ID_SESSION_ATTRIBUTE = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Check if the user is logged in by verifying the presence of a userId in the session
        UUID userId = (UUID) request.getSession().getAttribute(USER_ID_SESSION_ATTRIBUTE);

        if (userId == null) {
            // Redirect to login page if no user is logged in
            response.sendRedirect("/login");
            return false;
        }

        // Proceed if the user is logged in
        return true;
    }
}