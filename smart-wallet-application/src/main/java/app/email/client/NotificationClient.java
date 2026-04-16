package app.email.client;

import app.email.client.dto.NotificationPreferenceResponse;
import app.email.client.dto.UpsertNotificationPreference;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1/notifications/") // url-  това е основния endpoint на контролерав
                                                                                             // на notification-svc + останалите endpoint-и в контролера
                                                                                             // тук може да се сложи и url на външни API-s, т.е. да не са наши микросървизи
public interface NotificationClient {



    // Ще използвам този клиент, който ще изпълнява завки до микросървиза notification-svc
    //@FeignClient се използва за изпращане на зявки от един сървис до друг сървис

    @GetMapping("/test")
    ResponseEntity<String> getHelloWorld(@RequestParam(name = "name") String name);
    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreference notificationPreference);// когато ще изпращам тази POST заявка, искам да прикача към нея RequestBody, защото метода,
                                      // който обработва заявка в микросървиза очаква RequestBody


    // тук returntype - може на ResponseEntity, може да е Void, въпреки, че в микросървиза notification-svc е DTO,


    @GetMapping("/preferences")
    ResponseEntity<NotificationPreferenceResponse> getUserNotificationPreferences(@RequestParam(name = "userId") UUID userId);

}
