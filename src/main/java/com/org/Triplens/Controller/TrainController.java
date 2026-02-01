package com.org.Triplens.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.Services.TrainServices.TrainService;
import com.org.Triplens.entity.Train;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/trains")
public class TrainController {

	@Autowired
	private TrainService trainService;

	@PostMapping("/search")
	public List<Train> searchTrains(@RequestParam String origin, @RequestParam String destination) {
		return trainService.getTrains(origin, destination);
	}
}
