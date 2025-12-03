package app.wallet.service;

import app.exceptions.DomainException;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WalletService {
    private static final String SMART_WALLET_LTD = "Smart Wallet Ltd";

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public void createNewWallet(User user) {

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
