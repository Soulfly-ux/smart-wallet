package app.web;

import app.user.model.User;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String getLoginPage() {

        return "login";
    }

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
    public ModelAndView register(@Valid  RegisterRequest registerRequest) {
        User registeredUser = userService.register(registerRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("registerRequest", registeredUser);
        modelAndView.setViewName("login");


       return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage() {
        //искам да заредя тази страница с детайлите на потребителя, който е влезъл в тази страница
        // за това ни трябва ModelAndView, а не просто да показвам view:

        User userById = userService.getUserById(UUID.fromString("f6850b76-3848-40e2-97eb-f4a85c0f5452"));//копираме това id от базата
        // , за момента нямаме сесии и не знаем кой потребител се е логнал


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", userById);


        modelAndView.setViewName("home");

        return modelAndView;
    }


}
