package app.web;

import app.security.AuthenticationDetails;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import app.web.dto.TransferRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final UserService userService;
    private final WalletService walletService;
    @Autowired
    public TransferController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public ModelAndView getTransferPage(@AuthenticationPrincipal AuthenticationDetails userDetails) {



        User userById = userService.getUserById(userDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transfer");
        modelAndView.addObject("user", userById);
        modelAndView.addObject("transferRequest", TransferRequest.builder().build());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView transfer(@Valid TransferRequest transferRequest, BindingResult bindingResult,HttpSession session) {


//        1.Потребителя изпраща POST заявка за трансфериране на пари
//          2.Проверка на валидността на заявка с BindingResult
//          3.Ако заявката е невалидна, връща се на страницата за трансфериране с грешки
//          4.Ако заявката е валидна, се изпълнява трансферирането на пари
//          5.Връща се на страницата за КОНКРЕТНАТА транзакция - за това ни трябва id на транзакцията

        UUID userId = (UUID) session.getAttribute("user_id");
        User userById = userService.getUserById(userId);
        if (bindingResult.hasErrors()) {


            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("transfer");
            modelAndView.addObject("user", userById);
            modelAndView.addObject("transferRequest", transferRequest);

            return modelAndView;
        }
        Transaction transaction = walletService.transferFunds(userById,transferRequest);

        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }
}
