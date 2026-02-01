package com.org.Triplens.Services.TripCost;


import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class PricingService {

    public Map<String, Double> calculatePricing(double distance) {
        Map<String, Double> prices = new HashMap<>();

        prices.put("cheapest", distance * 2);
        prices.put("medium", distance * 3.2 * 4);
        prices.put("luxury", distance * 5);

        return prices;
    }
}
