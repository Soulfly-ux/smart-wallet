package app.wallet.service;

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

@Service
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void createNewWallet(User user) {

        Wallet wallet =  walletRepository.save(initializeWallet(user)) ;

        log.info("Wallet created: {}", wallet);

       ;
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
