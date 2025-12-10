package app.web;

import app.transaction.model.Transaction;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    @Autowired
    public TransactionController( TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @GetMapping()
    public ModelAndView getTransactionPage() {

        List<Transaction> allByOwnerId = transactionService.getAllByOwnerId(UUID.fromString("f6850b76-3848-40e2-97eb-f4a85c0f5452"));


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transactions");
        modelAndView.addObject("transactions", allByOwnerId);


        return modelAndView;
    }

    @GetMapping("/{id}") // връща транзакция за конкретно id
    public  ModelAndView getTransactionById(@PathVariable("id") UUID id) {

        return null;
    }
}
