package com.quizchii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QuizChiiApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizChiiApplication.class, args);
	}

}
