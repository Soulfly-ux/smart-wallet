package app.email.service;

import app.email.client.NotificationClient;
import app.email.client.dto.UpsertNotificationPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean isNotificationEnabled, String email) {

        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .notificationEnabled(isNotificationEnabled)
                .contactInfo(email)
                .type("EMAIL")
                .build();


        // Invoke FeignClient and execute the request
        ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(notificationPreference);
        if (!httpResponse.getStatusCode().is2xxSuccessful()) { // ако кода не е бил 200-299, това означава, че не е било успешно
            log.error("Notification preference could not be saved");
        } ; // НЕ ХВЪРЛЯМЕ EXCEPTION, ЗАЩОТО ПРЕФЕРЕНЦИИТЕ СА ЧАСТ ОТ МЕТОДА ЗА РЕГИСТРАЦИЯ
                                                                   // И АКО НЕ ПОЛУЧИМ КОД ЗА УСПЕШНА ЗАЯВКА ЗА ПРЕФЕРНЦИИТЕ, ПОТРЕБИТЕЛЯ НЯМА ДА СЕ РЕГИСТРИРА, А НИЕ ИСКАМЕ ДА СЕ РЕГИСТРИРА ДОРИ И ДА НЕ Е УСПЕШНА


    }

}
