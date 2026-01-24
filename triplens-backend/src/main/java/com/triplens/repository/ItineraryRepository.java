package com.triplens.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.triplens.model.Itinerary;

@Repository
public interface ItineraryRepository extends MongoRepository<Itinerary, ObjectId> {
}
