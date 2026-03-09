package com.example.cardNotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CardNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardNotificationApplication.class, args);
	}

}
