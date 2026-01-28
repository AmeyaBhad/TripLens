package com.org.Triplens.Services.Flight;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.org.Triplens.DTO.AeroFlightDTO;

import reactor.core.publisher.Mono;

@Service
public class FlightService {

//    @Autowired
//    private WebClient webClient;
	
    private final WebClient webClient;

    public FlightService(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public Mono<List<AeroFlightDTO.FlightData>> getFlightsByRoute(String originIata, String destIata, String date) {
        String fromTime = date + "T00:00";
        String toTime   = date + "T12:00"; // 12-hour window to avoid limits

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/flights/airports/iata/{code}/{fromTime}/{toTime}")
                        .queryParam("direction", "Departure")
                        .build(originIata, fromTime, toTime))
                .retrieve()
                // Map to the Wrapper class first
                .bodyToMono(AeroFlightDTO.Wrapper.class)
                // Extract the list
                .map(wrapper -> wrapper.getDepartures())
                // Filter: Keep flights where movement.airport.iata == destination
                .map(flights -> flights.stream()
                        .filter(f -> f.getMovement() != null 
                                  && f.getMovement().getAirport() != null
                                  && destIata.equalsIgnoreCase(f.getMovement().getAirport().getIata()))
                        .collect(Collectors.toList()));
    }
}