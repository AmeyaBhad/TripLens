package com.org.Triplens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "com.org.Triplens.config","com.org.Triplens.Controller", "com.org.Triplens.passwordEncryption",
		"com.org.Triplens.Services", "com.org.Triplens.DAO","com.org.Triplens.ingestion" })
@EnableMongoRepositories("com.org.Triplens.repository")
@org.springframework.boot.autoconfigure.domain.EntityScan("com.org.Triplens.entity")
@EnableAsync
@EnableScheduling
public class TriplensApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriplensApplication.class, args);
	}

}
