package app.web;

import app.email.client.dto.NotificationPreferenceResponse;
import app.email.service.NotificationService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getNotificationPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {


        User user = userService.getUserById(authenticationDetails.getUserId());

        NotificationPreferenceResponse notificationPreferences = notificationService.getNotificationPreferences(user.getId());

        ModelAndView modelAndView = new ModelAndView("notification");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreferences", notificationPreferences);


        return modelAndView;
    }
}
