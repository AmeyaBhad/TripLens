package com.org.Triplens.Services.StateExtractor;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CityStateResolver {

    private static final Map<String, String> CITY_TO_STATE = new HashMap<>();

    static {
        // Maharashtra
        CITY_TO_STATE.put("pune", "Maharashtra");
        CITY_TO_STATE.put("mumbai", "Maharashtra");
        CITY_TO_STATE.put("nagpur", "Maharashtra");
        CITY_TO_STATE.put("nashik", "Maharashtra");
        CITY_TO_STATE.put("aurangabad", "Maharashtra");

        // Karnataka
        CITY_TO_STATE.put("bengaluru", "Karnataka");
        CITY_TO_STATE.put("bangalore", "Karnataka");
        CITY_TO_STATE.put("mysuru", "Karnataka");
        CITY_TO_STATE.put("mangalore", "Karnataka");

        // Tamil Nadu
        CITY_TO_STATE.put("chennai", "Tamil Nadu");
        CITY_TO_STATE.put("coimbatore", "Tamil Nadu");
        CITY_TO_STATE.put("madurai", "Tamil Nadu");

        // Kerala
        CITY_TO_STATE.put("kochi", "Kerala");
        CITY_TO_STATE.put("ernakulam", "Kerala");
        CITY_TO_STATE.put("thiruvananthapuram", "Kerala");
        CITY_TO_STATE.put("trivandrum", "Kerala");

        // Delhi (UT)
        CITY_TO_STATE.put("delhi", "Delhi");

        // Telangana
        CITY_TO_STATE.put("hyderabad", "Telangana");

        // Andhra Pradesh
        CITY_TO_STATE.put("vijayawada", "Andhra Pradesh");
        CITY_TO_STATE.put("visakhapatnam", "Andhra Pradesh");

        // West Bengal
        CITY_TO_STATE.put("kolkata", "West Bengal");

        // Gujarat
        CITY_TO_STATE.put("ahmedabad", "Gujarat");
        CITY_TO_STATE.put("surat", "Gujarat");

        // Rajasthan
        CITY_TO_STATE.put("jaipur", "Rajasthan");
        CITY_TO_STATE.put("udaipur", "Rajasthan");

        // Uttar Pradesh
        CITY_TO_STATE.put("lucknow", "Uttar Pradesh");
        CITY_TO_STATE.put("varanasi", "Uttar Pradesh");
        CITY_TO_STATE.put("agra", "Uttar Pradesh");

        // Goa
        CITY_TO_STATE.put("panaji", "Goa");
        CITY_TO_STATE.put("margao", "Goa");
    }

    /**
     * Resolves city to Indian state.
     * Fallback is "India" if city not found.
     */
    public String resolveState(String city) {

        if (city == null || city.isBlank()) {
            return "India";
        }

        return CITY_TO_STATE.getOrDefault(
                city.trim().toLowerCase(),
                "India"
        );
    }
}

