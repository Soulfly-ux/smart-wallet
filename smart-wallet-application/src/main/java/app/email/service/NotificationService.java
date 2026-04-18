package app.email.service;

import app.email.client.NotificationClient;
import app.email.client.dto.NotificationPreferenceResponse;
import app.email.client.dto.NotificationResponse;
import app.email.client.dto.UpsertNotificationPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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



        if (email == null || email.isBlank()) {
            log.info("Skipping notification preference for user {} because email is missing", userId);
            return;
        }


        // Invoke FeignClient and execute the request
        ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(notificationPreference);
        if (!httpResponse.getStatusCode().is2xxSuccessful()) { // ако кода не е бил 200-299, това означава, че не е било успешно
            log.error("Notification preference could not be saved");
        } ; // НЕ ХВЪРЛЯМЕ EXCEPTION, ЗАЩОТО ПРЕФЕРЕНЦИИТЕ СА ЧАСТ ОТ МЕТОДА ЗА РЕГИСТРАЦИЯ
                                                                   // И АКО НЕ ПОЛУЧИМ КОД ЗА УСПЕШНА ЗАЯВКА ЗА ПРЕФЕРНЦИИТЕ, ПОТРЕБИТЕЛЯ НЯМА ДА СЕ РЕГИСТРИРА, А НИЕ ИСКАМЕ ДА СЕ РЕГИСТРИРА ДОРИ И ДА НЕ Е УСПЕШНА


    }

    public NotificationPreferenceResponse getNotificationPreferences(UUID userId) {

        // метод за показване на страницата с нотификации на даден потребител

        ResponseEntity<NotificationPreferenceResponse> httpResponse = notificationClient.getUserNotificationPreferences(userId);

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Notification preference for user id [%s] doesn't exist.".formatted(userId)); // тук можем да хвърлим EXCEPTION, защото не ни трябва да показваме нотификациите на даден потребител, ако той не ги е включил
        }

        return httpResponse.getBody();
    }

    public List<NotificationResponse> getNotificationHistory(UUID id) {

        ResponseEntity<List<NotificationResponse>> httpResponse = notificationClient.getNotificationHistory(id);

        return httpResponse.getBody();
    }

    public void sendNotification(UUID id, String moneyTransferSuccessful, String emailBody) {

    }
}
