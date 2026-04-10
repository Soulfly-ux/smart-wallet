package app.email.service;

import app.Application;
import app.email.client.NotificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestInit implements ApplicationRunner {

    private final NotificationClient notificationClient;
    @Autowired
    public TestInit(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        ResponseEntity<String> response = notificationClient.getHelloWorld("Kim");

        System.out.println(response.getBody());

    }
}
