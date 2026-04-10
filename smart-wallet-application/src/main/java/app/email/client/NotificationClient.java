package app.email.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1/notifications/") // url-  това е основния endpoint на контролерав
                                                                                             // на notification-svc + останалите endpoint-и в контролера
                                                                                             // тук може да се сложи и url на външни API-s, т.е. да не са наши микросървизи
public interface NotificationClient {

    // Ще използвам този клиент, който ще изпълнява завки до микросървиза notification-svc

    @GetMapping("/test")
    ResponseEntity<String> getHelloWorld(@RequestParam(name = "name") String name);
}
