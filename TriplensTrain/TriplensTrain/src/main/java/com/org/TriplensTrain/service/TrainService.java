package com.org.TriplensTrain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.TriplensTrain.Repository.TrainRepository;
import com.org.TriplensTrain.entity.Train;

@Service
public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public List<Train> searchTrains(String origin, String destination) {
        return trainRepository.findTrainsByRoute(origin, destination);
    }
}

