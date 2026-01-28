package com.org.Triplens.Services.Flight;

import org.springframework.stereotype.Service;

import com.org.Triplens.entity.Airport;
import com.org.Triplens.exception.FlightException.CityNotFoundException;
import com.org.Triplens.repository.airportrepository.AirportRepository;

@Service
public class AirportResolverService {

    private final AirportRepository airportRepository;

    public AirportResolverService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public String resolveCityToIata(String cityName) {
        return airportRepository
                .findByCityNameIgnoreCase(cityName.trim())
                .map(Airport::getIataCode)
                .orElseThrow(() ->
                        new CityNotFoundException(cityName)
                );
    }
}
