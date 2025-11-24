package app.user.service;

import app.exceptions.DomainException;
import app.subscription.service.SubscriptionService;
import app.user.model.Role;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.wallet.service.WalletService;
import app.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;// идва от spring security, но трябва да кажем каква имплементация искаме (package config), защото това е interface
    private final SubscriptionService subscriptionService;
    private final WalletService walletService;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
    }

    public User register(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());

        //validate username
        if (userOptional.isPresent()) {
            throw new DomainException("Username [%s] already exists.".formatted(registerRequest.getUsername()));
        }

        // Ако не съществува такова потребителско име , създаваме нов потребител(create new user account)


        User user = userRepository.save(initializeUser(registerRequest));

        // при създаване на нов потребител, правим  default wallet and subscription:

        walletService.createNewWallet(user);
        subscriptionService.createDefaultSubscription(user);

        //
        log.info("User [%s] created.", user.getUsername());
        return user;
    }





    private User initializeUser(RegisterRequest registerRequest) {
        // връща нов билднат потребител.билдваме това, което ще ни трябва при създаването на потребител, после можем да го променяме

        // трябва да енкриптнем паролата, за да не бъде видима в базата данни

        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER) // по default го създаваме с роля USER
                .isActive(true) // по default го създаваме със статус активен
                .country(registerRequest.getCountry())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
