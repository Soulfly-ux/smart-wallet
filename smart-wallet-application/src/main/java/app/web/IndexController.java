package app.web;

import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class IndexController {
    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public IndexController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }


//  @GetMapping("/")
//  public ModelAndView index() {
//
//    ModelAndView modelAndView = new ModelAndView();
//    modelAndView.setViewName("index");
//
//    return modelAndView;
//  }

    // втори вариант(по- подходящ) без ModelAndView:
    @GetMapping("/")
    public String getIndex() {


        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String error) {
       //required = false може да има ткъв параметър може и да няма.Има го ако има грешка при логване
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (error != null) {
            modelAndView.addObject("errorMessage", "Username or password are invalid.");
        }


        return modelAndView;
    }
    //HttpSession -> създава нова сесия за тази заявка (ако не съществува), тази сесия се генерира в момента в който, тя е autowired в параметрите на метода
//    @PostMapping("/login")
//    public ModelAndView login(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {
//
//        if (bindingResult.hasErrors()) {
//            return new ModelAndView("login");
//        }
//
//        User logedUser = userService.login(loginRequest);
//        session.setAttribute("user_id", logedUser.getId());
//        // можем да добавим колкото искаме атрибути на тази сесия (но обикновенно е достатъчно да добавим само id на потребителя), НИКОГА НЕ СЛАГАМЕ ДИРЕКТНО USER ОБЕКТА:
//        session.setAttribute("username", logedUser.getUsername());
//
//
//
//        // това е ако изпозваме куки(HttpResponse response) вместо сесия(HttpSession session)
//       // response.addCookie(new Cookie("user_id", logedUser.getId().toString()));// взимам id на потребителя и го запазвам в куки и го пращам на клиента
//
//
//
//        // знаем, че на home се реферира към user(има атрибути на този user), който е влязъл на  тази страница, за това правим:
//       // ако не го направим ще хвърли грешка, защото не може да парсне данните
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("user", logedUser);
//        modelAndView.setViewName("redirect:home");
//
//
//        return modelAndView;
//    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        // когато искам да заредя страницата за регистрация, освен да подам празна страница трябва да съм сигурен, че съм подал празно DTO
        // и за това правим:

        modelAndView.addObject("registerRequest", new RegisterRequest());
        modelAndView.setViewName("register");
        return modelAndView;

    }
    @PostMapping("/register")
    public ModelAndView register(@Valid  RegisterRequest registerRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return new ModelAndView("register"); // ако има грешки, ще се връща на страницата за регистрация
        }

        User registeredUser = userService.register(registerRequest);
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("registerRequest", registeredUser);


       return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        //искам да заредя тази страница с детайлите на потребителя, който е влезъл в тази страница
        // за това ни трябва ModelAndView, а не просто да показвам view:

        User userById = userService.getUserById(authenticationDetails.getUserId());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", userById);




        return modelAndView;
    }

//    @GetMapping("/logout")
//    public String getLogoutPage(HttpSession session) { // взимаме си сесията за да знаем кой се логаутва
//
//        session.invalidate();
//        return "redirect:/";
//    }


}
