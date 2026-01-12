package app.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldScheduler {

    //Scheduled Job every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void sayHelloEvery10Seconds() {

        System.out.println("Hello World");
    }
}
