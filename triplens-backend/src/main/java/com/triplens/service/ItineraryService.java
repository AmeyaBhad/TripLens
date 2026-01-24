package com.triplens.service;

import com.triplens.model.Itinerary;
import com.triplens.repository.ItineraryRepository;
import org.springframework.stereotype.Service;

@Service
public class ItineraryService {

    private final ItineraryRepository repository;

    public ItineraryService(ItineraryRepository repository) {
        this.repository = repository;
    }

    public Itinerary save(Itinerary itinerary) {
        return repository.save(itinerary);
    }
}
