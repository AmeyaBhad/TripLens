package com.org.Triplens.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.org.Triplens.entity.FestivalEntity;
@Repository
public interface FestivalRepository extends MongoRepository<FestivalEntity, String> {

	List<FestivalEntity> findByStateIgnoreCase(String state);

	boolean existsByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
}
