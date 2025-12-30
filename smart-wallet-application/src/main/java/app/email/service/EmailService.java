package app.email.service;

import app.web.dto.PaymentNotificationEvent;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendPaymentNotification(PaymentNotificationEvent event) {

        System.out.println("Charge happened for user with id: " + event.getUserId());

    }
}
