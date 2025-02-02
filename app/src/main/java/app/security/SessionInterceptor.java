package app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    private final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/", "/login", "/register");
    public static final String USER_ID_SESSION_ATTRIBUTE = "userId";

    // Този метод ще се изпълни преди всяка заявка
    // HttpServletRequest request - заявката, която се праща към нашето приложение
    // HttpServletResponse response - отговор, който връщаме
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String endpoint = request.getServletPath();
        if (UNAUTHENTICATED_ENDPOINTS.contains(endpoint)) {
            // Ако иска да достъпи ендпойнт, за който не ни трябва сесия, пускаме заявката напред да се обработи
            return true;
        }
        // Check if the user is logged in by verifying the presence of a userId in the session
        UUID userId = (UUID) request.getSession().getAttribute(USER_ID_SESSION_ATTRIBUTE);

        HttpSession currentUserSession = request.getSession(false);
        if (currentUserSession == null) {
            response.sendRedirect("/login");
            return false;

            // Proceed if the user is logged in
        }
        return true;
    }
}

