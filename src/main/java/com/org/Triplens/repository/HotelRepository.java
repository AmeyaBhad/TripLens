package com.org.Triplens.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.org.Triplens.entity.Hotel;

public interface HotelRepository extends MongoRepository<Hotel, ObjectId> {
	List<Hotel> findByCity(String city);
}
