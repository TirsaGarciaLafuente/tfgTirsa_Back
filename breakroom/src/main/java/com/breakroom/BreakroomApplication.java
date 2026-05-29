package com.breakroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 

@SpringBootApplication
@EnableScheduling 
public class BreakroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreakroomApplication.class, args);
	}

}