package app.tracking.service;

import app.web.dto.PaymentNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {


    @EventListener
    public void trackNewPayment(PaymentNotificationEvent event) {

        System.out.println("Payment tracking initiated for user with id: " + event.getUserId());
    }
}
