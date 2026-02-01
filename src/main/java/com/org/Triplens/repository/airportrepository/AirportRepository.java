package com.org.Triplens.repository.airportrepository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.org.Triplens.entity.Airport;

public interface AirportRepository
        extends MongoRepository<Airport, String> {

    Optional<Airport> findByCityNameIgnoreCase(String cityName);
}

