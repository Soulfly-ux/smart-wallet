package app.config;

import app.security.SessionCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private SessionCheckInterceptor interceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")// интерсептора се задейства за всички endpoint-и
                .excludePathPatterns("/css/**", "/js/**", "/images/**");
    }

    // Конфигурацията за Spring Security задължително трябва този BEAN
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
      // Всеки път при стартиране на приложеннието Spring Security проверява тази конфигурация и се съобразява с нея

        // описваме кои са endpoint-ите които очакваме един потребител да бъде аутентикиран
        //.authorizeHttpRequests-> конфиг. за група от endpoint-и
        //.requestMatchers -> достъп до даден endpoint
        http
                .authorizeHttpRequests(matchers -> matchers

                        .requestMatchers("/", "/register").permitAll()// всички endpoint-и с тези URL-и са разрешени за всеки
                        // login поведението идва наготово от Spring Security, ние не се грижим за него
                     //   .requestMatchers("/users").hasRole("ADMIN")// endpoint-ите с тези URL-и са разрешени само ако потребителят е администратор
                );




        return http.build();// връща някакъв SecurityFilterChain обект
    }
}
