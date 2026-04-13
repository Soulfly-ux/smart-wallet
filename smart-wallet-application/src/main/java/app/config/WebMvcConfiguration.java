package app.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@EnableWebSecurity
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
//    @Autowired
//    private SessionCheckInterceptor interceptor;


//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(interceptor)
//                .addPathPatterns("/**")// интерсептора се задейства за всички endpoint-и
//                .excludePathPatterns("/css/**", "/js/**", "/images/**");
//    }

    // Конфигурацията за Spring Security задължително трябва този BEAN
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
      // Всеки път при стартиране на приложеннието Spring Security проверява тази конфигурация и се съобразява с нея

        // описваме кои са endpoint-ите които очакваме един потребител да бъде аутентикиран
        //.authorizeHttpRequests-> конфиг. за група от endpoint-и
        //.requestMatchers -> достъп до даден endpoint
        //.permitAll() -> всички endpoint-и с тези URL-и са разрешени за всеки
        //.anyRequest() -> всички заявки които не съм изброил
        //.authenticated() -> за да имаш достъп трябва да се аутентикираш- (да се си влязъл в акаунта)
        http
                .authorizeHttpRequests(matchers -> matchers
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()// разреши всички заявки на статични ресурси на обичайните локации
                        .requestMatchers("/", "/register").permitAll()// всички endpoint-и с тези URL-и са разрешени за всеки
                        // login поведението идва наготово от Spring Security, ние не се грижим за него
                     //   .requestMatchers("/users").hasRole("ADMIN")// endpoint-ите с тези URL-и са разрешени само ако потребителят е администратор
                       // .requestMatchers("/users").hasAuthority("ADMIN")// endpoint-ите с тези URL-и са разрешени само ако потребителят е администратор

                        // НО ВМЕСТО ЗА ВСЕКИ ЕНДПОЙНТ, КОЙТО ДА СЕ ДОСТЪПВА САМО ОТ АДМИН ДА ПИШЕМ requestMatchers("/....").hasAuthority("ADMIN")
                        // ИЗПОЛЗВАМЕ METHOD SECURITY, работи на контролер ниво и няма нужда да идва всеки път да добавяме.hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form-> form
                        .loginPage("/login")// на кой ендпоинт ще се намира страницата
                        // МНОГО ВАЖНО: SS ще търси на тази страница username и password
                        // ако нашата логика е да се логваме с email и password например,
                        // тогава трябва изрично да му кажем .usernameParameter("email") и .passwordParameter("password")
                        .defaultSuccessUrl("/home")// на кой endpoint ще се връща след успешна логин
                        .failureUrl("/login?error")// на кой endpoint ще се връща след неуспешна логин
                        .permitAll()// всеки да може да използва логин страницата
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))// на кой endpoint ще се намира страницата
                        .logoutSuccessUrl("/")// ПРИ ЛОГАУТ ТРЯБВА ДА ПРАТИМ ПОТРЕБИТЕЛЯ НА СТРАНИЦА ПОЗВОЛЕНА ЗА ВСИЧКИ




                );










        return http.build();// връща някакъв SecurityFilterChain обект
    }
}
