package com.org.Triplens.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.org.Triplens.entity.Itinerary;

public interface ItineraryRepository extends MongoRepository<Itinerary, ObjectId> {

}
