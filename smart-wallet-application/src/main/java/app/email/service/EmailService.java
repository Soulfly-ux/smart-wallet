package app.email.service;

import app.web.dto.PaymentNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Async // This method will be executed asynchronously(не е задължително да се изпълнява веднага)
    @EventListener
    public void sendPaymentNotification(PaymentNotificationEvent event) {

        System.out.println("Charge happened for user with id: " + event.getUserId());

    }
}
