package app.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MyAPIController {

    @GetMapping("/info/1")
    public String getInformation1(HttpServletRequest request, HttpServletResponse response) {
        // изпълнението на  този метод ще започне, когато някой направи заявка до /api/v1.
        // Този метод дава достъп до HTTP заявката
        //HttpServletRequest ни дава пълен достъп на инфото за цялата заявка

        response.addHeader("X-My-Header", "Smart Wallet");
       // задавам cookies, които всеки път,когато клиент изпраща заявка, изпраща всеки път
        Cookie cookie = new Cookie("color", "Red");
        Cookie cookie1 = new Cookie("city", "Linz");
        response.addCookie(cookie);// закачаване на cookie към отговора
        response.addCookie(cookie1);// закачаване на cookie към отговора


        return "Information";
    }

    @GetMapping("/info/2")
    public String getInformation2(@CookieValue(value = "color", defaultValue = "Red") String color, @CookieValue(value = "city", defaultValue = "Berlin") String city) {

        return color + " " + city;
    }
}
