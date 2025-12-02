package app.user.service;

import app.user.model.Country;
import app.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component   // не е грешка да се използва @Service, но понеже няма бизнес логика , може да се използва и @Component
public class UserInit implements CommandLineRunner {

    // правим този клас , когато се стартира приложението, автоматично да се изпълни неговия run метод


    private final UserService userService;
    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        if(!userService.getAllUsers().isEmpty()) {
          return; //  прекратяваме метода ако има потребители
        }

        //(регистрираме, ако няма регистрирани потребители досега)
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("Kolio")
                    .password("123123")
                    .country(Country.BULGARIA)
                    .build(); // трябва да сложим @Builder, на класа RegisterRequest

            userService.register(registerRequest);

    }


}
