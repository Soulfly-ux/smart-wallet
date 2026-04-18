package app.web;

import app.email.client.dto.NotificationPreferenceResponse;
import app.email.client.dto.NotificationResponse;
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

import java.util.List;

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
        List<NotificationResponse> notificationHistory = notificationService.getNotificationHistory(user.getId());

        long succeededNotifications = notificationHistory.stream().filter(notification -> notification.getStatus().equals("SUCCEEDED")).count();
        long failedNotifications = notificationHistory.stream().filter(notification -> notification.getStatus().equals("FAILED")).count();
        notificationHistory = notificationHistory.stream().limit(5).toList();

        ModelAndView modelAndView = new ModelAndView("notification");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreferences", notificationPreferences);
        modelAndView.addObject("succeeded", succeededNotifications);
        modelAndView.addObject("failed", failedNotifications);
        modelAndView.addObject("notificationHistory", notificationHistory);


        return modelAndView;
    }
}
