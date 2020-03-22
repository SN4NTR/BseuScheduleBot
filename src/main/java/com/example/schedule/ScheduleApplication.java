package com.example.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class ScheduleApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(ScheduleApplication.class, args);
    }
}
