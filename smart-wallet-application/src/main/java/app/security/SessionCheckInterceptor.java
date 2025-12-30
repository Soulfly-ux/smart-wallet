package app.security;

import app.user.model.Role;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionCheckInterceptor implements HandlerInterceptor {

    private final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/login", "/register", "/"); // тези endpoint-и не са за аутентикация (не ни трябва сесия)
    private final Set<String> ADMIN_ENDPOINTS = Set.of("/users", "/reports");

    private final UserService userService;

    @Autowired
    public SessionCheckInterceptor(UserService userService) {
        this.userService = userService;
    }

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

        // проверка дали статуса на потребителя е активен
        // щом като сме минали горната проверка, сме сигурни, че имаме сесия
        UUID userId = (UUID) currentUserSession.getAttribute("user_id");
        User userById = userService.getUserById(userId); // взимаме потребителя, който се опитва да достъпи приложението

        if (!userById.isActive()){

            currentUserSession.invalidate();
            response.sendRedirect("/");
            return false; // не пускай потребителя нататък в приложението ако той е неактивен
        }
        // НАЧИН 1
        if (ADMIN_ENDPOINTS.contains(endpoint) && userById.getRole() != Role.ADMIN){
            // ако потребителят не е админ и опита да достъпи админски ресурс, връщаме false

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("You are not authorized to access this resource.");
            return false;
        }


        // НАЧИН 2

        HandlerMethod handlerMethod = (HandlerMethod) handler;

//
//        if(handlerMethod.hasMethodAnnotation(RequireAdminRole.class) && userById.getRole() != Role.ADMIN){ // проверка кой метод, (в случая get метода за users, който очакваме да се изпълни само от admin)
//                                                                                                         // има анотация RequireAdminRole
//
//            response.setStatus(HttpStatus.FORBIDDEN.value());
//            response.getWriter().write("You are not authorized to access this resource.");
//            return false;
//        }

        return true;
    }
}

