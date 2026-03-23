package com.vahabvahabov.phrase_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PhraseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhraseServiceApplication.class, args);
	}

}
