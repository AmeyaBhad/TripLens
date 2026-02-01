package com.org.Triplens.DAO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.entity.Train;
import com.org.Triplens.repository.TrainSearch.TrainRepository;

@Component
public class TrainDaoImpl implements TrainDao {

	@Autowired
	TrainRepository trainRepository;

	@Override
	public List<Train> getTrainsByOriginAndDestination(String origin, String destination) {

		return trainRepository.getTrainsByOriginAndDestination(origin, destination);
	}

}
