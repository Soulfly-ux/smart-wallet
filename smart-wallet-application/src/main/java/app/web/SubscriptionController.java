package app.web;

import app.security.AuthenticationDetails;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionType;
import app.subscription.service.SubscriptionService;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UpgradeRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ModelAndView upgradeSubscriptions(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
       // трябва ни сесия за да знаем, кой потребител иска да си ъпгрейдне абоннамента

        User userById = userService.getUserById(authenticationDetails.getUserId());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upgrade");
        modelAndView.addObject("user", userById);
        modelAndView.addObject("upgradeRequest", UpgradeRequest.builder().build());

        return modelAndView;
    }

    @PostMapping
    public String upgradeSubscriptions(@RequestParam("subscription-type")SubscriptionType subscriptionType, UpgradeRequest upgradeRequest,  @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        //@RequestParam("subscription-type") - така подавам типа на абонамента(DEFAULT, PREMIUM, ULTIMATE), имам три различни форми за тези абонаменти
//          за да знаем коя форма е избрана, '/subscriptions?subscription-type=DEFAULT' - това е пътя който слагаме в action на формата в upgrade.html
//        за следващата форма ще е '/subscriptions?subscription-type=PREMIUM'
//        th:if="${!(user.subscriptions.get(0).type.name())}" - get(0) е за да вземем първия абонамент на потребител, който е активния абонамент
//        тази проверка казва, ако не е на този абонамент, тогава може да си го купи


        User userById = userService.getUserById(authenticationDetails.getUserId());
        Transaction upgradeResult = subscriptionService.upgradeSubscription(userById, subscriptionType, upgradeRequest);


        return "redirect:/transactions/" + upgradeResult.getId();
    }



    @GetMapping("/history")
    public ModelAndView getHistoryPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User userById = userService.getUserById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
//
//        List<Subscription> allSubscriptions = subscriptionService.getSubscriptions(); не е нужен, защото user има subscriptions релация

        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("user", userById);

        return modelAndView;

    }
}
