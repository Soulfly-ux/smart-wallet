package app.web;

import app.exceptions.UserNameAlreadyExist;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.security.auth.login.AccountException;
import javax.swing.text.html.Option;

@ControllerAdvice
public class GlobalExceptionHandler {


    // 1 POST HTTP Request -> /register -> хвърля грешка и  redirect:/register
    // 2 GET HTTP Request -> /register -> register.html view

   // @ResponseStatus(HttpStatus.NOT_FOUND)// с тази анотация казваме какъв статус код да върне при response- a на този exception,ВАЖНО!!!- когато редиректваме НЕ СЛАГАМЕ тази анотация
    @ExceptionHandler(UserNameAlreadyExist.class)
    public String handleUserNameAlreadyExist(RedirectAttributes redirectAttributes, UserNameAlreadyExist exception) {

        // Option 1 HttpServletRequest request- Autowire като параметър на метода
        // Autowire HttpServletRequest request
        // Така взимаме името на потребителя, и да стане - "Amador is already in use":
//        String username = request.getParameter("username");
//        String message = "%s is already in use".formatted(username);


//        Option 2:
//        UserNameAlreadyExist exception- Autowire като параметър на метода
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", "This username is already in use!");
        // ако има exception това съобщение ще се изпише  при редирект в register.html, като добавяме атрибута "usernameAlreadyExistMessage" с thymeleaf в register.html
        //този redirectAttribute ще е валиден само при втората заявка и после изчезва




        return "redirect:/register";
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccountException.class,   // когато се опитва да достъпи ендпойнт, до който не му е позволено
            NoResourceFoundException.class, // когато се опитва да достъпи невалиден ендпойнт
            MethodArgumentTypeMismatchException.class
    })                                                       // този handler обработва няколко типа exceptions
    public ModelAndView handleNotFoundExceptions() {

        return new ModelAndView("not-found");

    }

    // Generic exception handler- за всички exceptions
    @ExceptionHandler
    public ModelAndView handleAnyException(Exception exception) {
        //обработва всеки Exception, който не сме обработили отделно
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("not-found"); // може да си направя и друг html за този метод
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());// exception.getClass().getSimpleName() - като поставим errorMessage в html, ще ни даде името на еxception-a

        return modelAndView;
    }
}
