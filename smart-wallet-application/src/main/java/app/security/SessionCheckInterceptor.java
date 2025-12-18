package app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class SessionCheckInterceptor implements HandlerInterceptor {

    private final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/login", "/register", "/"); // тези endpoint-и не са за аутентикация (не ни трябва сесия)


    //Този метод се изпълнява ПРЕДИ заявката да е изпълнена
    //HttpServletRequest request - заявката  която се прааща към на шето приложение
    //HttpServletResponse response - отговор, който връщаме
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //  взимаме Endpoint
        String endpoint = request.getServletPath();
        if (UNAUTHENTICATED_ENDPOINTS.contains(endpoint)) {
            return true; // пуска заявката напред да се обработи ако има някой от тези ендпойнти, за които не ни трябва сесия
        }

        // Ако заявката не е от тези ни трябва сесия
        // request.getSession() -> Вземам сесията, ако няма се създава нова!!!!
        //  request.getSession(false) -> върни сесия ако има,  ако няма сесия, не се създава, просто върни null
        HttpSession currentUserSession = request.getSession(false);

        if (currentUserSession == null) {
            response.sendRedirect("/login");// връща заявката към login страницата, и не го пускай напред след като няма сесия
            return false; // ако няма сесия, връща false, не пуска заявката напред
        }

        return true;
    }
}

