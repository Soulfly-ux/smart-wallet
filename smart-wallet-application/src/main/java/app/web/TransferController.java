package app.web;

import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import app.web.dto.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ModelAndView getTransferPage(@CookieValue("user_id") String userId) {
        User userById = userService.getUserById(UUID.fromString(userId));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transfer");
        modelAndView.addObject("user", userById);
        modelAndView.addObject("transferRequest", TransferRequest.builder().build());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView transfer(@Valid TransferRequest transferRequest, BindingResult bindingResult,@CookieValue("user_id") String userId) {


//        1.Потребителя изпраща POST заявка за трансфериране на пари
//          2.Проверка на валидността на заявка с BindingResult
//          3.Ако заявката е невалидна, връща се на страницата за трансфериране с грешки
//          4.Ако заявката е валидна, се изпълнява трансферирането на пари
//          5.Връща се на страницата за КОНКРЕТНАТА транзакция - за това ни трябва id на транзакцията
        User userById = userService.getUserById(UUID.fromString(userId));
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
