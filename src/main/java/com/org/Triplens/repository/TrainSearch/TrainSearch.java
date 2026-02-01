package com.org.Triplens.repository.TrainSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.org.Triplens.entity.Train;

@Component
public class TrainSearch implements TrainRepository {

	@Autowired
	MongoClient client;

	@Autowired
	MongoConverter mongoConvertor;

	@Override
	public List<Train> getTrainsByOriginAndDestination(String origin, String destination) {
		final List<Train> trains = new ArrayList<>();

		MongoDatabase database = client.getDatabase("TripLens");
		MongoCollection<Document> collection = database.getCollection("Train");
		AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search", 
			    new Document("index", "TrainIndex")
			            .append("compound", 
			    new Document("must", Arrays.asList(new Document("text", 
			                    new Document("query", origin)
			                            .append("path", Arrays.asList("origin_station", "intermediate_stations"))), 
			                    new Document("text", 
			                    new Document("query", destination)
			                            .append("path", Arrays.asList("destination_station", "intermediate_stations")))))))));
		result.forEach(doc -> {
			trains.add(mongoConvertor.read(Train.class, doc));
		});

		return trains;
	}

}
