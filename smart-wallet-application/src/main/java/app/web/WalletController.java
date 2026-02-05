package app.web;

import app.security.AuthenticationDetails;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    @Autowired
    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getWallet(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User userById = userService.getUserById(authenticationDetails.getUserId());

        Map<UUID, List<Transaction>> lastFourTransactionsPerWallet = walletService.getLastFourTransactions(userById.getWallets());
        //UUID -> ID на конкретен портфейл
        //List<Transaction>> -> последните четири транзакции на този конкретен портфейл


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallets");
        modelAndView.addObject("user", userById);
        modelAndView.addObject("lastFourTransactions", lastFourTransactionsPerWallet);

        return modelAndView;
    }


    @PostMapping
    public String createWallet(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        User userById = userService.getUserById(authenticationDetails.getUserId());
        walletService.unlockNewWallet(userById);
        return "redirect:/wallets";
    }
}
