package app.subscription.service;

import app.exceptions.DomainException;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import app.subscription.repository.SubscriptionRepository;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.user.model.User;
import app.wallet.service.WalletService;
import app.web.dto.UpgradeRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository; // взимаме репозитория за подписки
    private final WalletService walletService;
    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, WalletService walletService) {
        this.subscriptionRepository = subscriptionRepository;

        this.walletService = walletService;

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
    @Transactional
    public Transaction upgradeSubscription(User userById, SubscriptionType subscriptionType, UpgradeRequest upgradeRequest) {

        Optional<Subscription> optionalSubscription = subscriptionRepository.findByStatusAndOwnerId(SubscriptionStatus.ACTIVE, userById.getId());

        if(optionalSubscription.isEmpty()) { // ако няма активен абонамент
           throw new DomainException("You don't have active subscription.");
        }
        // Минали сме проверката, има активен абонамент:
        Subscription currentSubscription = optionalSubscription.get();

        SubscriptionPeriod subscriptionPeriod = upgradeRequest.getSubscriptionPeriod();
        BigDecimal subscriptionPrice = getSubscriptionPrice(subscriptionPeriod, subscriptionType);
        String type = subscriptionPeriod.name().substring(0, 1).toUpperCase() + subscriptionType.name().substring(1);
        String period = subscriptionPeriod.name().substring(0, 1).toUpperCase() + subscriptionPeriod.name().substring(1);
        String description = " Purchase of %s %s subscription".formatted(type, period);

        Transaction transactionResult = walletService.charge(userById, upgradeRequest.getWalletId(), subscriptionPrice, description);

        if (transactionResult.getTransactionStatus() == TransactionStatus.FAILED) {
            log.warn("Transaction failed: {}", transactionResult);
            return transactionResult;

        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime completedOn ;

        if (subscriptionPeriod == SubscriptionPeriod.MONTHLY){
            completedOn = now.plusMonths(1);
        }else {
            completedOn = now.plusYears(1);
        }


        // В противен случай връщаме новия абонамент
        Subscription newSubscription = Subscription.builder()
                .owner(userById)
                .status(SubscriptionStatus.ACTIVE)
                .period(subscriptionPeriod)
                .type(subscriptionType)
                .price(subscriptionPrice)
                .renewalAllowed(subscriptionPeriod == SubscriptionPeriod.MONTHLY)// ако е месечен абонамент, то системата го обновява, годишния потребителя, сам трябва да го обнови
                .createdOn(LocalDateTime.now())
                .completedOn(completedOn)
                .build();

        currentSubscription.setCompletedOn(now);
        currentSubscription.setStatus(SubscriptionStatus.COMPLETED);

        subscriptionRepository.save(currentSubscription);
        subscriptionRepository.save(newSubscription);

        return transactionResult;

    }

    private BigDecimal getSubscriptionPrice(SubscriptionPeriod subscriptionPeriod, SubscriptionType subscriptionType) {

        if (subscriptionType == SubscriptionType.DEFAULT){ // при този тип не ме интерсува периода, цената винаги е 0
                  return BigDecimal.ZERO;
        }else if (subscriptionType == SubscriptionType.PREMIUM && subscriptionPeriod == SubscriptionPeriod.MONTHLY){
            return BigDecimal.valueOf(19.99);

        }else if (subscriptionType == SubscriptionType.PREMIUM && subscriptionPeriod == SubscriptionPeriod.YEARLY){
            return BigDecimal.valueOf(199.99);

        }else if (subscriptionType == SubscriptionType.ULTIMATE && subscriptionPeriod == SubscriptionPeriod.MONTHLY){
            return BigDecimal.valueOf(49.99);

        }else{

            return BigDecimal.valueOf(499.99);

        }

    }
}
