package com.org.Triplens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = { "com.org.Triplens.Controller", "com.org.Triplens.passwordEncryption",
		"com.org.Triplens.Services", "com.org.Triplens.DAO" })
@EnableMongoRepositories("com.org.Triplens.repository")
@org.springframework.boot.autoconfigure.domain.EntityScan("com.org.Triplens.entity")
public class TriplensApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriplensApplication.class, args);
	}

}
