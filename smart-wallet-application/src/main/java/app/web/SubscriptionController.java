package app.web;

import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public String upgradeSubscriptions() {

        return "upgrade";
    }
    @GetMapping("/history")
    public ModelAndView getHistoryPage() {

        ModelAndView modelAndView = new ModelAndView();

        List<Subscription> allSubscriptions = subscriptionService.getSubscriptions();

        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("subscriptions", allSubscriptions);

        return modelAndView;

    }
}
