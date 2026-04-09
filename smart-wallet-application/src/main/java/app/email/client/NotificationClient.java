package app.email.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1/notifications/") // url-  това е основния endpoint на контролерав
                                                                                             // на notification-svc + останалите endpoint-и в контролера
public interface NotificationClient {

    // Ще използвам този клиент, който ще изпълнява завки до микросървиза notification-svc

    @GetMapping("/test")
    ResponseEntity<String> getHelloWorld();
}
