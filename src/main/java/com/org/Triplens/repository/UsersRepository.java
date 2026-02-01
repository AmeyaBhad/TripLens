package com.org.Triplens.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.org.Triplens.entity.Users;
@Repository
public interface UsersRepository extends MongoRepository<Users, ObjectId> {

	Optional<Users> findByEmail(String email);
}
