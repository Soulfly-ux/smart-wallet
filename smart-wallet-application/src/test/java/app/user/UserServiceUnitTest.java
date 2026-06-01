package app.user;

import app.email.service.NotificationService;
import app.exceptions.DomainException;
import app.exceptions.UserNameAlreadyExist;
import app.security.AuthenticationDetails;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.model.Country;
import app.user.model.Role;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.EditProfileRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//1. Create the test class
//2. Annotate the class with @ExtendWith(MockitoExtension.class)
//3. Get the class you want test and annotate
//4. Get all dependencies of that class and annotate them with @Mock
//5. Inject all those dependencies to the class with annotation @InjectMocks


// 1,2

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    // 4.
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private WalletService walletService;

    @Mock //Анотация за депендаситата в класа, който ще тествам
    private NotificationService notificationService;

    // 3, 5
    @InjectMocks // Анотация за класа който ще тествам
    private UserService userService;





    // Пример за параметаризиран тест
    @ParameterizedTest
    @MethodSource("userRolesArguments")
    void whenChangedUserRole_thenCorrectRoleIsAssigned(Role currrentUserRole, Role expectedUserRole) {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(currrentUserRole)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        // When
        userService.switchRole(userId);


        // Then
        assertEquals(expectedUserRole, user.getRole());
    }

    // Този метод е също за параметаризирания тест
    private static Stream<Arguments> userRolesArguments() {

        return Stream.of(
                Arguments.of(Role.USER, Role.ADMIN), // Ако сегашната роля е user очаквам да стане admin
                Arguments.of(Role.ADMIN,Role.USER) // Един път теста ще мине с пъвия от тези аргументи и след това с втория
        );
    }


    // Тест на гетър, който сме си направили
    @Test
    void givenUsersExistInDB_getAllUsers_thenReturnThem(){

        // Given
        List<User> userList = List.of(new User(), new User(), new User());// мокваме списък с трима потребителя
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<User> allUsers = userService.getAllUsers();

        // Then
        assertThat(allUsers).hasSize(3);

    }


    // Switch status
    @Test
    void givenUserWithStatusActive_whenSwitchStatus_thenUserBecomeInactive() {

        // Given
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .isActive(true)
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user)); // правим този when заради  User userById = getUserById(id); - това е в метода в сървиза

        // When
        userService.switchStatus(id);

        // Then
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);


    }

    @Test
    void givenUserWithStatusInactive_whenSwitchStatus_thenUserBecomeActive() {

        // Given
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .isActive(false)
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user)); // правим този when заради  User userById = getUserById(id); - това е в метода в сървиза

        // When
        userService.switchStatus(id);

        // Then
        assertTrue(user.isActive());
        verify(userRepository, times(1)).save(user);


    }



    // Register
    // Test 1: When username -> exception is thrown
    @Test
    void givenExistingUsername_whenRegister_thenExceptionIsThrown() {

       // Given
        String username = "Stamat";
        RegisterRequest dto = RegisterRequest.builder()
                .username(username)
                .country(Country.FRANCE)
                .password("123123")
                .build();


        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));


        // When & Then
        assertThrows(UserNameAlreadyExist.class,() -> userService.register(dto));

        // Правим тест за това, че след като има такъв потребител следните методи няма да бъдат извикани:
        verify(userRepository, never()).save(any());// any() - без значение с какъв параметър,тестваме че  просто не се стига до този метод
        verify(walletService,never()).initializeFirstWallet(any());
        verify(subscriptionService,never()).createDefaultSubscription(any());
        verify(notificationService,never()).saveNotificationPreference(any(UUID.class),anyBoolean(),anyString()); // може и само any





    }
    // Test 2: Happy path Registration
    @Test
    void givenHappyPath_whenRegister() {

        // Given
        String username = "Stamat";
        RegisterRequest dto = RegisterRequest.builder()
                .username(username)
                .country(Country.FRANCE)
                .password("123123")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID()) // id на потребителя ни трябва защото notificationService.saveNotificationPreference има за параметър id
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user); // Тук задължително трябва да върнем мокнат потребител, иначе теста ще изгурми с null
        when(subscriptionService.createDefaultSubscription(user)).thenReturn(new Subscription());
        when(walletService.initializeFirstWallet(user)).thenReturn(new Wallet());

        // When
        User registeredUser = userService.register(dto);


        // Then
       assertThat(registeredUser.getSubscriptions()).hasSize(1); // тест, че след регистрация се създават автоматично портфейл и абонамент
       assertThat(registeredUser.getWallets()).hasSize(1);

       verify(notificationService, times(1)).saveNotificationPreference(user.getId(), false, null);

    }






    // Test: When there is no user in DB (repo returns Optional.empty()) -> then expect an exception
    // of type DomainException
    @Test
    void givenMissingUserFromDatabase_whenEditUserDetails_thenExceptionIsThrown() {

        UUID userId = UUID.randomUUID();
        EditProfileRequest dto =  EditProfileRequest.builder().build();

        when(userRepository.findById(any())).thenReturn(Optional.empty()); // any()-> без значение какво id, може да се сложи като параметър и конкретно userId

       assertThrows(DomainException.class, () -> userService.editUserDetails(userId, dto)) ; // очаквам, че ще се хвърли този exception при извикването на editUserDetails, защото на горния ред сме казали така

    }

    // Test Case: When DB returns user object -> then change their details from dto whith email address (Метод EditUserDetails)
    // and save notification preference and save the user to the database
    @Test
    void  givenExistingUser_wheEditTheirProfileWithActualEmail_thenChangeTheirDetailsSavePreferenceAndSaveToDatabase() {

        // Given - подготвям сами сценарий
        UUID userId = UUID.randomUUID();
        EditProfileRequest dto =  EditProfileRequest.builder()
                .firstName("Manol")
                .lastName("Manolov")
                .email("Manol@abv.bg")
                .profilePictureUrl("www.image.bg")
                .build();

        User user = User.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When - извиквам метода който ще тествам
        userService.editUserDetails(userId, dto);



        // Then - assertions
        assertEquals("Manol", user.getFirstName());
        assertEquals("Manolov", user.getLastName());
        assertEquals("Manol@abv.bg", user.getEmail());
        assertEquals("www.image.bg", user.getProfilePicture());
        // с verify(нещо се е  изпълнило) се обръщаме към мокнат обект, times(1) - метода се е извикал веднъж
        // и без verify тестовете ще минат, но е добра практика да се използват- това са методи към мокнати обекти  в метода който тестваме
        //verify също част от assertions
        verify(notificationService, times(1)).saveNotificationPreference(userId,true, dto.getEmail());

        verify(userRepository).save(user);

    }

    @Test
    void  givenExistingUser_wheEditTheirProfileEmptyEmail_thenChangeTheirDetailsSavePreferenceAndSaveToDatabase() {

        // Given - подготвям сами сценарий
        UUID userId = UUID.randomUUID();
        EditProfileRequest dto =  EditProfileRequest.builder()
                .firstName("Manol")
                .lastName("Manolov")
                .email("")
                .profilePictureUrl("www.image.bg")
                .build();

        User user = User.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When - извиквам метода който ще тествам
        userService.editUserDetails(userId, dto);



        // Then - assertions
        assertEquals("Manol", user.getFirstName());
        assertEquals("Manolov", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("www.image.bg", user.getProfilePicture());
        // с verify(нещо се е  изпълнило) се обръщаме към мокнат обект, times(1) - метода се е извикал веднъж
        // и без verify тестовете ще минат, но е добра практика да се използват- това са методи към мокнати обекти  в метода който тестваме
        //verify също част от assertions
        verify(notificationService, times(1)).saveNotificationPreference(userId,false, null);

        verify(userRepository).save(user);

    }
    // Test 1: When user exist- then return  AuthenticationDetails
    @Test
    void shouldReturnAuthenticationDetails_whenUserExist() {

        // Given
        UUID userId = UUID.randomUUID();
        String username = "Stamat";
        User user = User.builder()
                .id(userId)
                .username(username)
                .password("123123")
                .role(Role.USER)
                .isActive(true)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));


        // When
       UserDetails authenticationDetails = userService.loadUserByUsername(username);

        // Then
        assertInstanceOf(AuthenticationDetails.class, authenticationDetails);
        AuthenticationDetails result = (AuthenticationDetails) authenticationDetails;
        assertEquals(user.getId(),result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.isActive(), result.isActive());

        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());


    }


    // Test 1: When user does not exist- then throw new DomainException
    @Test
    void givenMissingUserFromDB_whenLoadUserByUsername_thenExceptionIsThrown() {

        // Given
        String username = "Stamat";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        // When & Then
        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));

    }


}
