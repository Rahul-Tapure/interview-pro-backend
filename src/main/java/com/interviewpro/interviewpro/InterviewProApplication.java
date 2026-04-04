package com.interviewpro.interviewpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class InterviewProApplication {
	public static void main(String[] args) {
		

		Dotenv.configure().ignoreIfMissing().load();
		
		SpringApplication.run(InterviewProApplication.class, args);
	}

}
