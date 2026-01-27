package com.org.TriplensTrain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.org.TriplensTrain.Repository.TrainRepository;

@SpringBootApplication
public class TriplensTrainApplication {
	
	@Autowired
	TrainRepository trainRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(TriplensTrainApplication.class, args);
	}

}
