package com.org.TriplensTrain.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.org.TriplensTrain.entity.Train;

@Repository
public interface TrainRepository extends MongoRepository<Train, String> {
	
    @Query(value = "{" +
            " $and: [" +
            "   { $or: [ { origin_station: ?0 }, { intermediate_stations: ?0 } ] }," +
            "   { $or: [ { destination_station: ?1 }, { intermediate_stations: ?1 } ] }" +
            " ]" +
            "}")
    List<Train> findTrainsByRoute(String origin, String destination);	
}
