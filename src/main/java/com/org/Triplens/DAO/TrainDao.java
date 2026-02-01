package com.org.Triplens.DAO;

import java.util.List;

import com.org.Triplens.entity.Train;

public interface TrainDao {
	List<Train> getTrainsByOriginAndDestination(String origin, String destination);
}
