package com.trip.tripcost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.trip")
public class TripcostApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripcostApplication.class, args);
	}

}
