package com.org.Triplens.Controller.FlightController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.DTO.AeroFlightDTO;
import com.org.Triplens.Services.Flight.AirportResolverService;
import com.org.Triplens.Services.Flight.FlightService;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private AirportResolverService airportResolverService;
    
//    @GetMapping("/search")
//    public Mono<List<AeroFlightDTO.FlightData>> searchFlights(
//            @RequestParam String origin,
//            @RequestParam String destination,
//            @RequestParam String date) {
//    	
//    		
//        return flightService.getFlightsByRoute(from, to, date);
//    }
    
    @GetMapping("/search")
    public List<AeroFlightDTO.FlightData> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {

        String fromIata = airportResolverService.resolveCityToIata(from);
        String toIata = airportResolverService.resolveCityToIata(to);

        return flightService
                .getFlightsByRoute(fromIata, toIata, date)
                .block(); // OK here since MVC thread model
    }

    
}