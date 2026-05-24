package com.breakroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 1. Añadimos esta importación

@SpringBootApplication
@EnableScheduling // 2. Añadimos esta anotación
public class BreakroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreakroomApplication.class, args);
	}

}