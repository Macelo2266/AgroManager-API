package com.maceloaraujo.AgroManager.API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AgroManagerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgroManagerApiApplication.class, args);
	}

}
