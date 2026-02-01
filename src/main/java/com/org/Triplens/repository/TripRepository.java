package com.org.Triplens.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.org.Triplens.entity.Trip;
@Repository
public interface TripRepository extends MongoRepository<Trip, ObjectId> {

	List<Trip> findByOwnerUserIdOrSharedUsersUserId(ObjectId userId, ObjectId userId2);

}
