package app.web;

import app.security.AuthenticationDetails;
import app.transaction.model.Transaction;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {


    private final TransactionService transactionService;
    private final UserService userService;
    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {

        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView getTransactionPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        List<Transaction> allByOwnerId = transactionService.getAllByOwnerId(authenticationDetails.getUserId());
        User user = userService.getUserById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transactions");
        modelAndView.addObject("transactions", allByOwnerId);
        modelAndView.addObject("user", user);


        return modelAndView;
    }

    @GetMapping("/{id}") // връща транзакция за конкретно id
    public  ModelAndView getTransactionById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        Transaction transactionById = transactionService.getTransactionById(id);
        User user = userService.getUserById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaction-result");
        modelAndView.addObject("transaction", transactionById);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
