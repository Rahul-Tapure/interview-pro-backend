package com.interviewpro.interviewpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class InterviewProApplication {
	public static void main(String[] args) {
		

        // Load .env file
        Dotenv dotenv = Dotenv.load();

        // Set values into system properties
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        
		SpringApplication.run(InterviewProApplication.class, args);
	}

}
