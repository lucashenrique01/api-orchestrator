package com.poc.apiorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ApiOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOrchestratorApplication.class, args);
	}

}
