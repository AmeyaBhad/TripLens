package com.org.TriplensTrain.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.TriplensTrain.entity.Train;
import com.org.TriplensTrain.service.TrainService;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/search")
    public List<Train> search(
            @RequestParam String origin,
            @RequestParam String destination) {
        return trainService.searchTrains(origin, destination);
    }
}

