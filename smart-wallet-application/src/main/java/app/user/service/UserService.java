package app.user.service;

import app.email.service.NotificationService;
import app.exceptions.DomainException;
import app.security.AuthenticationDetails;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.model.Role;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.EditProfileRequest;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;// идва от spring security, но трябва да кажем каква имплементация искаме (package config), защото това е interface
    private final SubscriptionService subscriptionService;
    private final WalletService walletService;
    private final NotificationService notificationService;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, WalletService walletService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
        this.notificationService = notificationService;
    }



//    public User login(LoginRequest loginRequest) {
//
//        // проверяваме дали има потребител с това потребителско име
//
//        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
//
//        if (optionalUser.isEmpty()) {
//            throw new DomainException("Username or password are invalid.");// добре е тук да не даваме пряка информация кое от двете не е вярно.
//        }
//
//        // проверяваме дали паролата е вярна
//        User user = optionalUser.get();// .get() - връща това което Optional- a пази
//        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) { // passwordEncoder.matches( чистата парола, енкоднатата парола в базата данни)
//            throw new DomainException("Username or password are invalid.");
//        }
//
//        return user;
//    }


    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());

        //validate username
        if (userOptional.isPresent()) {
            throw new DomainException("Username [%s] already exists.".formatted(registerRequest.getUsername()));
        }

        // Ако не съществува такова потребителско име , създаваме нов потребител(create new user account)


        User user = userRepository.save(initializeUser(registerRequest));

        // при създаване на нов потребител, правим  default wallet and subscription:

        Wallet standartWallet = walletService.initializeFirstWallet(user);
        user.setWallets(List.of(standartWallet));


        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(user);
        user.setSubscriptions(List.of(defaultSubscription));

        //Persist new notification preference with  isEnabled = false; когато се регистрира потребител, е с настройка за изключени нотификации
        notificationService.saveNotificationPreference(
                user.getId(),
                false,
                null
        );

     /*   UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId())
                .createdOn(user.getCreatedOn())
                .build();
        userRegisteredEventProducer.sendEvent(event);*/

        //
        log.info("User {} created.", user.getUsername());
        return user;
    }





    public User initializeUser(RegisterRequest registerRequest) {
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
    @CacheEvict(value = "users", allEntries = true)
    public void editUserDetails(UUID id, EditProfileRequest editProfileRequest) {
        User userById = getUserById(id);

        // Ако няма имейл, нотификациите не са позволени
        if (editProfileRequest.getEmail().isBlank()) {

            notificationService.saveNotificationPreference(
                    userById.getId(),
                    false,
                    null
            );

        }

     userById.setFirstName(editProfileRequest.getFirstName());
     userById.setLastName(editProfileRequest.getLastName());
     userById.setEmail(editProfileRequest.getEmail());
     userById.setProfilePicture(editProfileRequest.getProfilePictureUrl());

        userRepository.save(userById);

        // След добавяне на имейл нотификациите автоматично стават позволени
        if (!editProfileRequest.getEmail().isBlank()) {
            notificationService.saveNotificationPreference(
                    userById.getId(),
                    true,
                    editProfileRequest.getEmail()
            );


        }


    }
     // В началото метода се изпълнява веднъж и после са пази в кеша
    // Всяко следващо извикване резултатът се връща от кеша и няма да се  извиква от базата данни
    @Cacheable("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] not found".formatted(id)));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID id) {

        User userById = getUserById(id);

        // първи начин по -опростен
//        if(userById.isActive()){
//            userById.setActive(false);
//        }else{
//            userById.setActive(true);
//        }

        // втори начин(ако е активен направи го да е неактивен )
        userById.setActive(!userById.isActive()); // така може само ако променливата е boolean



        userRepository.save(userById);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID id) {

        User userById = getUserById(id);

        if (userById.getRole() == Role.USER) {
            userById.setRole(Role.ADMIN);
        }else {
            userById.setRole(Role.USER);
        }

        userRepository.save(userById);
    }

    // Всеки път когато потребител се логва спринг секюрити ще извиква този метод за да вземе детайлите за потребителя с този username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // тук е логиката за логване на потребител.Нашата вече не ни трябва.Логването вече е в ръцете на spring security
        // ако логиката ни е да се логваме с e-mail, а не username , като параметър тук подаваме e-mail, за да може Spring Security да търси потребител по e-mail ,
        // като използваме .findByЕmail() вместо .findByUsername()

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("Username [%s] not found".formatted(username)));// това е логика за това, че ако не съществува потребител с това потребителско име, да

        return new AuthenticationDetails(user.getId(), username, user.getPassword(), user.getRole(), user.isActive()); // това е моя UserDetails, там пазя данните на моя потребител
    }
}
