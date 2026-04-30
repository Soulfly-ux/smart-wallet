package app.web;

import app.exceptions.UserNameAlreadyExist;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNameAlreadyExist.class)
    public ModelAndView handleUserNameAlreadyExist() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("not-found");


        return modelAndView;
    }
}
