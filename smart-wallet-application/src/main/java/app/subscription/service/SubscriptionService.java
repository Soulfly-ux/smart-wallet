package app.subscription.service;

import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import app.subscription.repository.SubscriptionRepository;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository; // взимаме репозитория за подписки
    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;

    }

    public void createDefaultSubscription(User user) { // User user- subscription за конкретен user

        Subscription subscription = subscriptionRepository.save( initializeSubscription(user)); // сейвам преди лога зашото за да взема ид
                                                                                              // , трябва да съм го записал в базата
        log.info("Successfully create new subscription with id [%s] and type [%s]".formatted(subscription.getId(), subscription.getType()));



    }

    private Subscription initializeSubscription(User user) {
        LocalDateTime now = LocalDateTime.now();

        return Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.MONTHLY)
                .type(SubscriptionType.DEFAULT)
                .price(new BigDecimal(0))
                .renewalAllowed(true)
                .createdOn(now)
                .completedOn(now.plusMonths(1))
                .build();
    }

    public List<Subscription> getSubscriptions() {

        return subscriptionRepository.findAll();
    }
}
