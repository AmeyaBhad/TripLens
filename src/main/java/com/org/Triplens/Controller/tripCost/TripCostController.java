package com.org.Triplens.Controller.tripCost;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.trip.dto.TripResponseDTO;
import com.org.Triplens.DTO.TripCostRequestDTO;
import com.org.Triplens.DTO.TripCostResponseDTO;
import com.org.Triplens.Services.TripCost.DistanceService;
import com.org.Triplens.Services.TripCost.GeoCodingService;
import com.org.Triplens.Services.TripCost.PricingService;
//import com.trip.service.*;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin(origins = "http://localhost:3000")
public class TripCostController {

    private final GeoCodingService geoCodingService;
    private final DistanceService distanceService;
    private final PricingService pricingService;

    public TripCostController(
        GeoCodingService geoCodingService,
        DistanceService distanceService,
        PricingService pricingService
    ) {
        this.geoCodingService = geoCodingService;
        this.distanceService = distanceService;
        this.pricingService = pricingService;
    }

    @PostMapping("/calculate")
    public TripCostResponseDTO calculate(@RequestBody TripCostRequestDTO request) {

        double[] src = geoCodingService.getCoordinates(request.getSource());
        double[] dest = geoCodingService.getCoordinates(request.getDestination());

        double distance = distanceService.getDistance(src, dest);

        TripCostResponseDTO response = new TripCostResponseDTO();
        response.setDistanceKm(distance);
        response.setPricing(pricingService.calculatePricing(distance));

        return response;
    }
}
