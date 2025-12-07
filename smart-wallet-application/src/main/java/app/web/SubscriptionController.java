package app.web;

import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final SubscriptionService subscriptionService;
    private final UserService userService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    @GetMapping
    public String upgradeSubscriptions() {

        return "upgrade";
    }
    @GetMapping("/history")
    public ModelAndView getHistoryPage() {


        User userById = userService.getUserById(UUID.fromString("f6850b76-3848-40e2-97eb-f4a85c0f5452"));

        ModelAndView modelAndView = new ModelAndView();
//
//        List<Subscription> allSubscriptions = subscriptionService.getSubscriptions();

        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("user", userById);

        return modelAndView;

    }
}
