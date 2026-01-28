package com.org.Triplens.Services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TouristSpotService {

    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
    private final String OPEN_METEO_API = "https://geocoding-api.open-meteo.com/v1/search";
    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
    private final String USER_AGENT = "TripLens/1.0 (contact@triplens.org)"; // Good practice to id your bot

    public List<Map<String, String>> getNearbySpots(String location) {
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, String>> candidates = new ArrayList<>();

        // 1. GET THE "GOLD LIST"
        List<String> priorityKeywords = getCitySpecificKeywords(location);

        try {
            // 2. Get Coordinates
            double[] coords = getCoordinatesWithBackup(location, restTemplate);
            if (coords == null) {
                return createErrorList("Location Error", "Could not find coordinates.");
            }

            // 3. Fetch Data
            String wikiUrl = WIKI_API + "?action=query&generator=geosearch" +
                    "&ggscoord=" + coords[0] + "|" + coords[1] +
                    "&ggsradius=10000" +      
                    "&ggslimit=50" +          
                    "&prop=pageimages|extracts|info" + 
                    "&pithumbsize=600" +      
                    "&exintro=true" +         
                    "&explaintext=true" +     
                    "&exsentences=2" +        
                    "&format=json";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(wikiUrl, HttpMethod.GET, entity, String.class);
            
            // Safety check for empty body
            if (response.getBody() == null) return createErrorList("Error", "Empty response from Wikipedia");
            
            JSONObject root = new JSONObject(response.getBody());

            if (root.has("query") && root.getJSONObject("query").has("pages")) {
                JSONObject pages = root.getJSONObject("query").getJSONObject("pages");
                Iterator<String> keys = pages.keys();

                while (keys.hasNext()) {
                    JSONObject page = pages.getJSONObject(keys.next());
                    String title = page.optString("title");
                    String lowerTitle = title.toLowerCase();
                    String extract = page.optString("extract", "").toLowerCase();

                    if (lowerTitle.equalsIgnoreCase(location.toLowerCase())) continue;

                    // --- HARD BLOCKERS ---
                    if (isBlocked(lowerTitle, extract)) continue;

                    // --- SCORING SYSTEM ---
                    int score = page.optInt("length", 0); 

                    // 1. GOLD LIST BOOST
                    for (String keyword : priorityKeywords) {
                        if (lowerTitle.contains(keyword)) {
                            score += 10000000;
                            break; 
                        }
                    }

                    // 2. Image Bonus
                    if (page.has("thumbnail")) score += 50000;

                    // 3. Generic Boosts
                    score += getGenericBoost(lowerTitle);

                    Map<String, String> map = new HashMap<>();
                    map.put("name", title);
                    map.put("description", page.optString("extract", "A famous tourist destination."));
                    map.put("distance_km", "In " + location);
                    map.put("type", "Top Attraction");
                    
                    if (page.has("thumbnail")) {
                        map.put("image_url", page.getJSONObject("thumbnail").optString("source"));
                    } else {
                        map.put("image_url", "https://via.placeholder.com/400x300?text=No+Image");
                    }
                    map.put("score", String.valueOf(score)); 
                    candidates.add(map);
                }
            }

            candidates.sort((a, b) -> Integer.parseInt(b.get("score")) - Integer.parseInt(a.get("score")));

            // Filter Duplicates and limit to 5
            List<Map<String, String>> finalResult = new ArrayList<>();
            for (Map<String, String> candidate : candidates) {
                if (finalResult.size() >= 5) break;

                String newName = candidate.get("name").toLowerCase();
                boolean isDuplicate = false;

                for (Map<String, String> existing : finalResult) {
                    String existingName = existing.get("name").toLowerCase();
                    if (newName.contains(existingName) || existingName.contains(newName)) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    candidate.remove("score");
                    finalResult.add(candidate);
                }
            }
            
            return finalResult.isEmpty() ? createErrorList("No Data", "Wiki returned no results.") : finalResult;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorList("Error", e.getMessage());
        }
    }

    // Helper method to clean up the main logic loop
    private boolean isBlocked(String lowerTitle, String extract) {
        if (lowerTitle.contains("district") || lowerTitle.contains("division")) return true;
        if (lowerTitle.contains("constituency") || lowerTitle.contains("lok sabha")) return true;
        if (lowerTitle.contains("assembly") || lowerTitle.contains("vidhan sabha")) return true;
        if (lowerTitle.contains("municipal") || lowerTitle.contains("corporation")) return true;
        if (lowerTitle.contains("headquarters") || lowerTitle.contains("cantonment")) return true;
        if (lowerTitle.contains("school") || lowerTitle.contains("college")) return true;
        if (lowerTitle.contains("university") || lowerTitle.contains("institute")) return true;
        if (lowerTitle.contains("hospital") || lowerTitle.contains("court")) return true;
        if (lowerTitle.contains("station") || lowerTitle.contains("railway")) return true;
        if (lowerTitle.contains("metro") || lowerTitle.contains("airport")) return true;
        if (lowerTitle.contains("geography")) return true;
        if (extract.contains("is a taluka") || extract.contains("is a town")) return true;
        if (extract.contains("census town") || extract.contains("administrative")) return true;
        return false;
    }

    private int getGenericBoost(String lowerTitle) {
        int score = 0;
        if (lowerTitle.contains("fort")) score += 200000;
        if (lowerTitle.contains("temple") || lowerTitle.contains("mandir")) score += 200000;
        if (lowerTitle.contains("museum")) score += 150000;
        if (lowerTitle.contains("palace")) score += 150000;
        if (lowerTitle.contains("garden") || lowerTitle.contains("park")) score += 100000;
        if (lowerTitle.contains("market")) score += 100000;
        if (lowerTitle.contains("lake")) score += 100000;
        return score;
    }

    // ... (Keep your getCitySpecificKeywords, getCoordinatesWithBackup, etc. logic exactly as is below) ...
    // Paste the rest of your helper methods here: getCitySpecificKeywords, getCoordinatesWithBackup, etc.
    // I have omitted them for brevity but assume they exist exactly as you wrote them.
    
    // --- TOP 30 CITIES DATASET (For context) ---
    private List<String> getCitySpecificKeywords(String city) {
        String c = city.toLowerCase().trim();
        List<String> keywords = new ArrayList<>();
        // ... paste your massive if/else block here ...
        if (c.contains("agra")) {
             keywords.addAll(Arrays.asList("taj mahal", "agra fort", "mehtab bagh", "tomb of itimad-ud-daulah"));
        }
        // ... etc ...
        return keywords;
    }

    private double[] getCoordinatesWithBackup(String location, RestTemplate restTemplate) {
        double[] coords = getCoordsFromOSM(location, restTemplate);
        if (coords != null) return coords;
        return getCoordsFromOpenMeteo(location, restTemplate);
    }

    private double[] getCoordsFromOSM(String location, RestTemplate restTemplate) {
        try {
            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = OSM_API + "?q=" + encodedLoc + "&format=json&limit=1";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JSONArray array = new JSONArray(response.getBody());
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                return new double[]{obj.getDouble("lat"), obj.getDouble("lon")};
            }
        } catch (Exception e) {}
        return null;
    }

    private double[] getCoordsFromOpenMeteo(String location, RestTemplate restTemplate) {
        try {
            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = OPEN_METEO_API + "?name=" + encodedLoc + "&count=1&language=en&format=json";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject root = new JSONObject(response.getBody());
            if (root.has("results")) {
                JSONObject obj = root.getJSONArray("results").getJSONObject(0);
                return new double[]{obj.getDouble("latitude"), obj.getDouble("longitude")};
            }
        } catch (Exception e) {}
        return null;
    }

    private List<Map<String, String>> createErrorList(String name, String desc) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", desc);
        list.add(map);
        return list;
    }
}