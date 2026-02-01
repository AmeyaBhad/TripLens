package com.org.Triplens.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.org.Triplens.entity.TouristSpotCollection;

@Repository
public interface TouristSpotRepository extends MongoRepository<TouristSpotCollection, String> {
    // We can add a finder here to avoid re-scraping if we already have data for a city
    TouristSpotCollection findByLocationIgnoreCase(String location);
}