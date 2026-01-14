package app.scheduler;

import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionType;
import app.subscription.service.SubscriptionService;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.user.model.User;
import app.web.dto.UpgradeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class SubscriptionRenewalScheduler {
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionRenewalScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

 //   @Scheduled(fixedRate = 20000)// на всеки 20 секунди
    public void renewSubscriptions() {

        List<Subscription> subscriptions = subscriptionService.getAllSubscriptionsReadyForRenewal();

        if (subscriptions.isEmpty()) {
            log.info("No subscriptions to renew.");

            return;
        }

            for (Subscription subscription : subscriptions) {

                if (subscription.isRenewalAllowed()) {


                    User owner = subscription.getOwner();
                    SubscriptionType type = subscription.getType();
                    SubscriptionPeriod period = subscription.getPeriod();
                    UUID walletId = owner.getWallets().get(0).getId();// дай ми първия портфейл

                    UpgradeRequest request = new UpgradeRequest();
                    request.setSubscriptionPeriod(period);
                    request.setWalletId(walletId);


                    Transaction transaction = subscriptionService.upgradeSubscription(owner, type, request);
                   if (transaction.getTransactionStatus() == TransactionStatus.FAILED) {
                       // фейлва, например защото няма достатъчно пари

                       subscriptionService.markSubscriptionAsTerminated(subscription);
                       subscriptionService.createDefaultSubscription(subscription.getOwner());

                   }



                }else {
                   subscriptionService.markSubscriptionAsCompleted(subscription); // ако не може да се обнови
                   subscriptionService.createDefaultSubscription(subscription.getOwner()); //   създавам нов default абонамент
                }

            }


    }
}
