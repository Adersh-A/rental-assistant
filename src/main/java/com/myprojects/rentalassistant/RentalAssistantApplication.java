package com.myprojects.rentalassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RentalAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentalAssistantApplication.class, args);
	}

}
