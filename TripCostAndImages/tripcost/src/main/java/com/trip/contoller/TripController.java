package com.trip.contoller;


import com.trip.dto.TripRequestDTO;
import com.trip.dto.TripResponseDTO;
import com.trip.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin(origins = "http://localhost:3000")
public class TripController {

    private final GeoCodingService geoCodingService;
    private final DistanceService distanceService;
    private final PricingService pricingService;

    public TripController(
        GeoCodingService geoCodingService,
        DistanceService distanceService,
        PricingService pricingService
    ) {
        this.geoCodingService = geoCodingService;
        this.distanceService = distanceService;
        this.pricingService = pricingService;
    }

    @PostMapping("/calculate")
    public TripResponseDTO calculate(@RequestBody TripRequestDTO request) {

        double[] src = geoCodingService.getCoordinates(request.getSource());
        double[] dest = geoCodingService.getCoordinates(request.getDestination());

        double distance = distanceService.getDistance(src, dest);

        TripResponseDTO response = new TripResponseDTO();
        response.setDistanceKm(distance);
        response.setPricing(pricingService.calculatePricing(distance));

        return response;
    }
}
