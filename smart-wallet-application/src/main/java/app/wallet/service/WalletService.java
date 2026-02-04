package app.wallet.service;

import app.exceptions.DomainException;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionType;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import app.web.dto.PaymentNotificationEvent;
import app.web.dto.TransferRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.awt.font.MultipleMaster;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WalletService {
    private static final String SMART_WALLET_LTD = "Smart Wallet Ltd";

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService, ApplicationEventPublisher eventPublisher) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;

        this.eventPublisher = eventPublisher;
    }

    public void unlockNewWallet(User user) {
        List<Wallet> allByOwnerUsername = walletRepository.findAllByOwnerUsername(user.getUsername());
        Subscription activeSubscription = user.getSubscriptions().get(0);//get(0)-> последния(активния) създаден абонамент

//        Pesho
//        Subs: Ultimate
//        Wallets: 1     при тези условия и трите проверки ще са false

//        Goshho
//        Subs: Premium
//        Wallets: 2    при тези условия втората проверка е true и няма да се отключи нов портфейл


        // ако тези булеви = true , не може да се отключи нов портфейл
        boolean isDefaultPlanAndMaxWalletUnlocked = activeSubscription.getType() == SubscriptionType.DEFAULT && allByOwnerUsername.size() == 1;//ако е активен план и портфейлът е един
        boolean isPremiumPlanAndMaxWalletUnlocked = activeSubscription.getType() == SubscriptionType.PREMIUM && allByOwnerUsername.size() == 2;
        boolean isUltimatePlanAndMaxWalletUnlocked = activeSubscription.getType() == SubscriptionType.ULTIMATE && allByOwnerUsername.size() == 3;

        if (isDefaultPlanAndMaxWalletUnlocked || isPremiumPlanAndMaxWalletUnlocked || isUltimatePlanAndMaxWalletUnlocked) {
            throw new DomainException("You have reached the maximum number of wallets.");
        }


        Wallet newWallet = Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(BigDecimal.valueOf(0))
                .currency(Currency.getInstance("EUR"))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        walletRepository.save(newWallet);

    }

    public void initializeFirstWallet(User user) {
      //този метод се използва сама за създаване на първия портфейл при регистрация, а не и за добавяне на нов по- късно

        List<Wallet> userWalletList = walletRepository.findAllByOwnerUsername(user.getUsername());
       //  не може да се създаде с този метод втори портфейл, освен при регистрацията на потребителя
        if (!userWalletList.isEmpty()) {

            throw new DomainException("This user already has a wallet. First wallet already exists.");
        }
        Wallet wallet =  walletRepository.save(initializeWallet(user)) ;

        log.info("Wallet created: {}", wallet);

       
    }

    
    public Transaction transferMoney(UUID walletId, BigDecimal amount) {
        
        // взимаме портфейла по UUID:
        Wallet wallet = findWalletById(walletId);

        String transactioDescription = "Top up %.2f.".formatted(amount.doubleValue());

        // проверяваме дали портфейлът е активен, и ако не е транзакцията фейлва:
        if (wallet.getStatus() == WalletStatus.INACTIVE) {
            return  transactionService.createTransaction(wallet.getOwner(),   // тук са всички полета на Transaction
                   SMART_WALLET_LTD, // изпращача на парите
                    walletId.toString(), // получателя на парите
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.DEPOSIT,  // тип на транзакцията
                    TransactionStatus.FAILED, // защото не може да се изппълни тази транзакция
                    transactioDescription,
                    "Inactive wallet");
        }

        wallet.setBalance(wallet.getBalance().add(amount));// взимам досегашния баланс и добавяме пари( amount - параметър на метода transferMoney)
        wallet.setUpdatedOn(LocalDateTime.now()); // обновявам датата на актуализацията на портфейла, важно да се обнови дата на актуализацията

        walletRepository.save(wallet); // съхранявам новия портфейл в с променени полета баланс и датата на актуализацията
        return transactionService.createTransaction(wallet.getOwner(), SMART_WALLET_LTD,
                walletId.toString(),
                amount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                transactioDescription,
                null);

    }


    public Transaction transferFunds(User sender, TransferRequest transferRequest) {
       // трябва да изпълня transferRequest-а за този потребител- userById:
        // 1. взимаме портфейла по UUID от който потребителя ще си изпраща парите:
        Wallet senderWallet = findWalletById(transferRequest.getFromWalletId()); // взимам портфейлът по UUID (първото поле на TransferRequest)

        // 2. взимаме портфейла(трябва да е активен) по UUID на потребителя който ще получава парите (второто поле на TransferRequest):

        Optional<Wallet> receiverWallet = walletRepository.findAllByOwnerUsername(transferRequest.getToUsername())  // взимам портфейлът по username-> връща списък, трябва да си взема един от тях
                .stream()
                .filter(wallet -> wallet.getStatus() == WalletStatus.ACTIVE)
                .findFirst();

        String transferDescription = "Transfer from %s to %s for %.2f EUR".formatted(sender.getUsername(), transferRequest.getToUsername(), transferRequest.getAmount());

        // Ако няма активен портфейл, то създавам  транзакция със статус FAILED:
        if (receiverWallet.isEmpty()) {
            return transactionService.createTransaction(sender,
                                                    senderWallet.getId().toString(),
                    transferRequest.getToUsername(),
                    transferRequest.getAmount(),
                    senderWallet.getBalance(),
                    senderWallet.getCurrency(),
                    TransactionType.WITHDRAWAL, // тип на транзакцията
                    TransactionStatus.FAILED,
                    "Inactive wallet",
                    "Invalid criteria for transfer ");// пишем това, защото не искаме да издаваме инфо, дали има потребител и т.н
        }
        //Минали сме проверката дали има такъв портфейл , ако мине следователно има
        //Money Transfer
        // Ivan-> Gosho |20 EUR парите на Ижан ги намалявам в метода charge
        // Ivan - 20 EUR, Gosho + 20



        Transaction withdrawal = charge(sender, senderWallet.getId(), transferRequest.getAmount(), transferDescription);
        if (withdrawal.getTransactionStatus() == TransactionStatus.FAILED) { // цялата логика за един портфейл,  дали  транзакциаята е failed проверяваме в метода charge
            return withdrawal;
        }

        Wallet activeReceiverWallet = receiverWallet.get();
        activeReceiverWallet.setBalance(activeReceiverWallet.getBalance().add(transferRequest.getAmount()));// увеличавам баланa (Gosho + 20 EUR)на този който получава парите
        activeReceiverWallet.setUpdatedOn(LocalDateTime.now());

        walletRepository.save(activeReceiverWallet);
        // в долния метод създаваме транзакция на този който изпраща парите
         //на тук на този който ги получава:

        transactionService.createTransaction(
                receiverWallet.get().getOwner(),
                senderWallet.getId().toString(),
                receiverWallet.get().getId().toString(),
                transferRequest.getAmount(),
                receiverWallet.get().getBalance(),
                receiverWallet.get().getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                transferDescription,
                null
        );

                  return withdrawal;

    }
    @Transactional
    public Transaction charge(User user,UUID walletId, BigDecimal amount, String description) {

        Wallet wallet = findWalletById(walletId);// от този портфейл ще се намалят парите-> charge(такса)


        boolean isFailedTransaction = false;
        String failureReason = null;
        if(wallet.getStatus() == WalletStatus.INACTIVE) {
            // при тази валидация не хвърляма грешка, а правим тази транзакция като FAILED
             failureReason = "Inactive wallet";
            isFailedTransaction = true;

        }


        if(wallet.getBalance().compareTo(amount) < 0) {// по този начин се сравняват два BigDecimal ако е true баланса е по-малък от amount
            failureReason = "No enough money";
            isFailedTransaction = true;
        }

        if (isFailedTransaction) { // ако някоя от горните проверки е true, то транзакцията е фейлва(isFailedTransaction вече е true)

            return transactionService.createTransaction(user, walletId.toString(),
                    SMART_WALLET_LTD, // кой взима парите
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.WITHDRAWAL, // тип на транзакцията
                    TransactionStatus.FAILED,
                    description,
                    failureReason);//правим такава транзакция , когато статуса е неактивен и не променяме баланса

        }
        // ако двете проверки са false, то транзакцията е успешна и променяме баланса
        wallet.setBalance(wallet.getBalance().subtract(amount));// взимам досегашния баланс и изваждам пари( amount - параметър на метода transferMoney)
        wallet.setUpdatedOn(LocalDateTime.now());

        walletRepository.save(wallet); // съхранявам новия портфейл в с променени полета баланс и датата на актуализацията

        // Успешно плащане

        PaymentNotificationEvent event = PaymentNotificationEvent.builder()// публикувам този евент и който има интерес нека слуша за него
                .userId(user.getId())                            // ASYNC vs SYNC     - SYNC-> когато един клас публикува евент, той чака всички listeners да изпълнят своите задачи
                                                                 // , преди да продължат нататък. Всичко това се случва в една нишка,
                .email(user.getEmail())
                .amount(amount)
                .paymentTime(LocalDate.now())
                .build();

//        emailService.sendPaymentNotification(event); вместо да използваме тези сървизи, като депенденси, използвам ApplicationEventPublisher,
//                                                      защото ако в един момент решим да премахнем trackingService по някаква причина
//                                                      walletService няма да работи без тази зависимост, така по този начин walletService не знае за съществуването на тези два сервиса
//                                                      Трябва да накараме тези два сървиза да слушат за евента (слагаме @Eventlisener над методите в тези класове),
//                                                      когато се публикува обект  в случая тов е PaymentNotificationEvent и да слушат за него
//        trackingService.trackNewPayment(event);

        eventPublisher.publishEvent(event);

       return transactionService.createTransaction(user, walletId.toString(),
               SMART_WALLET_LTD,
               amount,
               wallet.getBalance(),
               wallet.getCurrency(),
               TransactionType.WITHDRAWAL,
               TransactionStatus.SUCCEEDED,
               description,
               null);
    }

    public Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId).orElseThrow(() -> new DomainException("Wallet with id [%s]not found".formatted(walletId)));
    }

    private Wallet initializeWallet(User user) {
        return Wallet.builder()
                .owner(user)
                .balance(BigDecimal.valueOf(20))
                .status(WalletStatus.ACTIVE)
                .currency(Currency.getInstance("EUR"))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
