package com.org.Triplens.repository.TrainSearch;

import java.util.List;

import com.org.Triplens.entity.Train;

public interface TrainRepository {
	List<Train> getTrainsByOriginAndDestination(String origin, String destination);
}
