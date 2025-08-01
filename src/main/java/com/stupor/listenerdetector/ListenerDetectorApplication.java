package com.stupor.listenerdetector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ListenerDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListenerDetectorApplication.class, args);
    }

}
