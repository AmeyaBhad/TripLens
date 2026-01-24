package com.triplens.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TouristSpotService {

    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
    private final String OPEN_METEO_API = "https://geocoding-api.open-meteo.com/v1/search";
    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public List<Map<String, String>> getNearbySpots(String location) {
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, String>> candidates = new ArrayList<>();

        // 1. GET THE "GOLD LIST" (Now covers 30+ Cities)
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
                    if (lowerTitle.contains("district") || lowerTitle.contains("division")) continue;
                    if (lowerTitle.contains("constituency") || lowerTitle.contains("lok sabha")) continue;
                    if (lowerTitle.contains("assembly") || lowerTitle.contains("vidhan sabha")) continue;
                    if (lowerTitle.contains("municipal") || lowerTitle.contains("corporation")) continue;
                    if (lowerTitle.contains("headquarters") || lowerTitle.contains("cantonment")) continue;
                    if (lowerTitle.contains("school") || lowerTitle.contains("college")) continue;
                    if (lowerTitle.contains("university") || lowerTitle.contains("institute")) continue;
                    if (lowerTitle.contains("hospital") || lowerTitle.contains("court")) continue;
                    if (lowerTitle.contains("station") || lowerTitle.contains("railway")) continue;
                    if (lowerTitle.contains("metro") || lowerTitle.contains("airport")) continue;
                    if (lowerTitle.contains("geography")) continue;
                    
                    if (extract.contains("is a taluka") || extract.contains("is a town")) continue;
                    if (extract.contains("census town") || extract.contains("administrative")) continue;

                    // --- SCORING SYSTEM ---
                    int score = page.optInt("length", 0); 

                    // 1. GOLD LIST BOOST (10 Million Points)
                    for (String keyword : priorityKeywords) {
                        if (lowerTitle.contains(keyword)) {
                            score += 10000000;
                            break; 
                        }
                    }

                    // 2. Image Bonus
                    if (page.has("thumbnail")) score += 50000;

                    // 3. Generic Boosts
                    if (lowerTitle.contains("fort")) score += 200000;
                    if (lowerTitle.contains("temple") || lowerTitle.contains("mandir")) score += 200000;
                    if (lowerTitle.contains("museum")) score += 150000;
                    if (lowerTitle.contains("palace")) score += 150000;
                    if (lowerTitle.contains("garden") || lowerTitle.contains("park")) score += 100000;
                    if (lowerTitle.contains("market")) score += 100000;
                    if (lowerTitle.contains("lake")) score += 100000;

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

    // --- TOP 30 CITIES DATASET ---
    private List<String> getCitySpecificKeywords(String city) {
        String c = city.toLowerCase().trim();
        List<String> keywords = new ArrayList<>();

        if (c.contains("agra")) {
            keywords.addAll(Arrays.asList("taj mahal", "agra fort", "mehtab bagh", "tomb of itimad-ud-daulah"));
        } else if (c.contains("jaipur")) {
            keywords.addAll(Arrays.asList("hawa mahal", "amber fort", "city palace", "jantar mantar", "jal mahal", "nahargarh"));
        } else if (c.contains("delhi") || c.contains("new delhi")) {
            keywords.addAll(Arrays.asList("india gate", "red fort", "qutub minar", "humayun", "lotus temple", "akshardham", "jama masjid"));
        } else if (c.contains("mumbai")) {
            keywords.addAll(Arrays.asList("gateway of india", "marine drive", "elephanta", "juhu", "siddhivinayak", "colaba"));
        } else if (c.contains("kolkata") || c.contains("calcutta")) {
            keywords.addAll(Arrays.asList("victoria memorial", "howrah bridge", "dakshineswar", "indian museum", "eden gardens", "kalighat"));
        } else if (c.contains("bangalore") || c.contains("bengaluru")) {
            keywords.addAll(Arrays.asList("lalbagh", "cubbon park", "bangalore palace", "iskcon", "bannerghatta"));
        } else if (c.contains("hyderabad")) {
            keywords.addAll(Arrays.asList("charminar", "golconda", "hussain sagar", "ramoji", "chowmahalla"));
        } else if (c.contains("chennai") || c.contains("madras")) {
            keywords.addAll(Arrays.asList("marina beach", "kapaleeshwarar", "fort st. george", "guindy", "san thome"));
        } else if (c.contains("ahmedabad")) {
            keywords.addAll(Arrays.asList("sabarmati", "kankaria", "adalaj", "sidi saiyyed"));
        } else if (c.contains("pune")) {
            keywords.addAll(Arrays.asList("shaniwar wada", "aga khan", "sinhagad", "dagadusheth", "pataleshwar"));
        } else if (c.contains("goa") || c.contains("panaji")) {
            keywords.addAll(Arrays.asList("basilica of bom jesus", "calangute", "baga", "fort aguada", "dudhsagar"));
        } else if (c.contains("varanasi") || c.contains("kashi")) {
            keywords.addAll(Arrays.asList("kashi vishwanath", "dashashwamedh", "sarnath", "assi ghat", "manikarnika"));
        } else if (c.contains("udaipur")) {
            keywords.addAll(Arrays.asList("city palace", "lake pichola", "jag mandir", "saheliyon-ki-bari", "fateh sagar"));
        } else if (c.contains("amritsar")) {
            keywords.addAll(Arrays.asList("golden temple", "jallianwala bagh", "wagah", "durgiana"));
        } else if (c.contains("jodhpur")) {
            keywords.addAll(Arrays.asList("mehrangarh", "umaid bhawan", "jaswant thada", "mandore"));
        } else if (c.contains("kerala") || c.contains("kochi")) {
            keywords.addAll(Arrays.asList("fort kochi", "mattancherry", "chinese fishing nets", "marine drive"));
        } else if (c.contains("mysore") || c.contains("mysuru")) {
            keywords.addAll(Arrays.asList("mysore palace", "brindavan", "chamundi", "st. philomena"));
        } else if (c.contains("rishikesh")) {
            keywords.addAll(Arrays.asList("laxman jhula", "ram jhula", "triveni ghat", "parmarth niketan"));
        } else if (c.contains("shimla")) {
            keywords.addAll(Arrays.asList("the ridge", "mall road", "jakhu", "christ church"));
        } else if (c.contains("manali")) {
            keywords.addAll(Arrays.asList("solang", "rohtang", "hidimba", "jogini"));
        } else if (c.contains("darjeeling")) {
            keywords.addAll(Arrays.asList("tiger hill", "batasia", "ghoom", "tea garden"));
        } else if (c.contains("ooty")) {
            keywords.addAll(Arrays.asList("botanical garden", "ooty lake", "doddabetta", "rose garden"));
        } else if (c.contains("nashik")) {
            keywords.addAll(Arrays.asList("trimbakeshwar", "pandavleni", "sula", "kalaram", "panchavati"));
        } else if (c.contains("visakhapatnam") || c.contains("vizag")) {
            keywords.addAll(Arrays.asList("rk beach", "submarine museum", "kailasagiri", "rushikonda"));
        } else if (c.contains("lucknow")) {
            keywords.addAll(Arrays.asList("bara imambara", "rumi darwaza", "hazratganj", "ambedkar park"));
        } else if (c.contains("bhubaneswar") || c.contains("puri")) {
            keywords.addAll(Arrays.asList("jagannath", "konark", "lingaraj", "udayagiri"));
        } else if (c.contains("indore")) {
            keywords.addAll(Arrays.asList("rajwada", "sarafa", "lal bagh palace", "khajrana"));
        } else if (c.contains("patna")) {
            keywords.addAll(Arrays.asList("golghar", "patna sahib", "buddha smriti", "patna museum"));
        } else if (c.contains("bhopal")) {
            keywords.addAll(Arrays.asList("upper lake", "van vihar", "sanchi", "bhimbetka"));
        } else if (c.contains("surat")) {
            keywords.addAll(Arrays.asList("dumas", "dutch garden", "sarthana"));
        }
        
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




//package com.triplens.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//public class TouristSpotService {
//
//    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
//    private final String OPEN_METEO_API = "https://geocoding-api.open-meteo.com/v1/search";
//    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
//    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
//
//    public List<Map<String, String>> getNearbySpots(String location) {
//        RestTemplate restTemplate = new RestTemplate();
//        List<Map<String, String>> candidates = new ArrayList<>();
//
//        // 1. GET THE "GOLD LIST" FOR THIS CITY
//        // If we know the city, we get specific keywords. If not, we get an empty list.
//        List<String> priorityKeywords = getCitySpecificKeywords(location);
//
//        try {
//            // 2. Get Coordinates
//            double[] coords = getCoordinatesWithBackup(location, restTemplate);
//            if (coords == null) {
//                return createErrorList("Location Error", "Could not find coordinates.");
//            }
//
//            // 3. Fetch Data (Limit 50)
//            String wikiUrl = WIKI_API + "?action=query&generator=geosearch" +
//                    "&ggscoord=" + coords[0] + "|" + coords[1] +
//                    "&ggsradius=10000" +     
//                    "&ggslimit=50" +         
//                    "&prop=pageimages|extracts|info" + 
//                    "&pithumbsize=600" +     
//                    "&exintro=true" +        
//                    "&explaintext=true" +    
//                    "&exsentences=2" +       
//                    "&format=json";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(wikiUrl, HttpMethod.GET, entity, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//
//            if (root.has("query") && root.getJSONObject("query").has("pages")) {
//                JSONObject pages = root.getJSONObject("query").getJSONObject("pages");
//                Iterator<String> keys = pages.keys();
//
//                while (keys.hasNext()) {
//                    JSONObject page = pages.getJSONObject(keys.next());
//                    String title = page.optString("title");
//                    String lowerTitle = title.toLowerCase();
//                    String extract = page.optString("extract", "").toLowerCase();
//
//                    // --- FILTER 1: Skip City Name ---
//                    if (lowerTitle.equalsIgnoreCase(location.toLowerCase())) continue;
//
//                    // --- FILTER 2: Hard Blockers ---
//                    if (lowerTitle.contains("district") || lowerTitle.contains("division")) continue;
//                    if (lowerTitle.contains("constituency") || lowerTitle.contains("lok sabha")) continue;
//                    if (lowerTitle.contains("assembly") || lowerTitle.contains("vidhan sabha")) continue;
//                    if (lowerTitle.contains("municipal") || lowerTitle.contains("corporation")) continue;
//                    if (lowerTitle.contains("headquarters") || lowerTitle.contains("cantonment")) continue;
//                    if (lowerTitle.contains("school") || lowerTitle.contains("college")) continue;
//                    if (lowerTitle.contains("university") || lowerTitle.contains("institute")) continue;
//                    if (lowerTitle.contains("hospital") || lowerTitle.contains("court")) continue;
//                    if (lowerTitle.contains("station") || lowerTitle.contains("railway")) continue;
//                    if (lowerTitle.contains("metro") || lowerTitle.contains("airport")) continue;
//                    if (lowerTitle.contains("geography")) continue;
//                    
//                    if (extract.contains("is a taluka") || extract.contains("is a town")) continue;
//                    if (extract.contains("census town") || extract.contains("administrative")) continue;
//
//                    // --- SCORING SYSTEM ---
//                    int score = page.optInt("length", 0); 
//
//                    // 1. "GOLD LIST" CHECK (The Specific Score Boost)
//                    // If the title matches our hardcoded favorites, give it 10 MILLION points.
//                    // This guarantees it will be #1.
//                    for (String keyword : priorityKeywords) {
//                        if (lowerTitle.contains(keyword)) {
//                            score += 10000000; // Massive Boost
//                            break; 
//                        }
//                    }
//
//                    // 2. Image Bonus
//                    if (page.has("thumbnail")) score += 50000;
//
//                    // 3. General Keyword Boosts (Fallback for unknown cities)
//                    if (lowerTitle.contains("fort")) score += 200000;
//                    if (lowerTitle.contains("temple") || lowerTitle.contains("mandir")) score += 200000;
//                    if (lowerTitle.contains("museum")) score += 150000;
//                    if (lowerTitle.contains("palace")) score += 150000;
//                    if (lowerTitle.contains("garden") || lowerTitle.contains("park")) score += 100000;
//                    if (lowerTitle.contains("market")) score += 100000;
//                    if (lowerTitle.contains("lake")) score += 100000;
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", title);
//                    map.put("description", page.optString("extract", "A famous tourist destination."));
//                    map.put("distance_km", "In " + location);
//                    map.put("type", "Top Attraction");
//                    
//                    if (page.has("thumbnail")) {
//                        map.put("image_url", page.getJSONObject("thumbnail").optString("source"));
//                    } else {
//                        map.put("image_url", "https://via.placeholder.com/400x300?text=No+Image");
//                    }
//                    map.put("score", String.valueOf(score)); 
//                    candidates.add(map);
//                }
//            }
//
//            // 3. Sort by Score
//            candidates.sort((a, b) -> Integer.parseInt(b.get("score")) - Integer.parseInt(a.get("score")));
//
//            // 4. Return Top 5
//            List<Map<String, String>> finalResult = new ArrayList<>();
//            for (Map<String, String> candidate : candidates) {
//                if (finalResult.size() >= 5) break;
//
//                String newName = candidate.get("name").toLowerCase();
//                boolean isDuplicate = false;
//
//                for (Map<String, String> existing : finalResult) {
//                    String existingName = existing.get("name").toLowerCase();
//                    if (newName.contains(existingName) || existingName.contains(newName)) {
//                        isDuplicate = true;
//                        break;
//                    }
//                }
//
//                if (!isDuplicate) {
//                    candidate.remove("score");
//                    finalResult.add(candidate);
//                }
//            }
//            
//            return finalResult.isEmpty() ? createErrorList("No Data", "Wiki returned no results.") : finalResult;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createErrorList("Error", e.getMessage());
//        }
//    }
//
//    // --- YOUR NEW FEATURE: THE GOLD LIST ---
//    private List<String> getCitySpecificKeywords(String city) {
//        String c = city.toLowerCase().trim();
//        List<String> keywords = new ArrayList<>();
//
//        if (c.contains("jaipur")) {
//            keywords.add("hawa mahal");
//            keywords.add("amber fort");
//            keywords.add("city palace");
//            keywords.add("jantar mantar");
//            keywords.add("albert hall");
//        } else if (c.contains("agra")) {
//            keywords.add("taj mahal");
//            keywords.add("agra fort");
//            keywords.add("mehtab bagh");
//        } else if (c.contains("mumbai")) {
//            keywords.add("gateway of india");
//            keywords.add("marine drive");
//            keywords.add("elephanta");
//            keywords.add("cst"); // Chhatrapati Shivaji Terminus
//        } else if (c.contains("delhi")) {
//            keywords.add("india gate");
//            keywords.add("red fort");
//            keywords.add("qutub minar");
//            keywords.add("humayun");
//            keywords.add("lotus temple");
//        } else if (c.contains("kolkata") || c.contains("calcutta")) {
//            keywords.add("victoria memorial");
//            keywords.add("howrah bridge");
//            keywords.add("dakshineswar");
//            keywords.add("indian museum");
//        } else if (c.contains("pune")) {
//            keywords.add("shaniwar wada");
//            keywords.add("aga khan palace");
//            keywords.add("dagadusheth");
//            keywords.add("sinhagad");
//        } else if (c.contains("nashik")) {
//            keywords.add("trimbakeshwar");
//            keywords.add("pandavleni"); // Caves
//            keywords.add("sula"); // Vineyards
//            keywords.add("kalaram");
//            keywords.add("panchavati");
//        }
//        
//        return keywords;
//    }
//
//    private double[] getCoordinatesWithBackup(String location, RestTemplate restTemplate) {
//        double[] coords = getCoordsFromOSM(location, restTemplate);
//        if (coords != null) return coords;
//        return getCoordsFromOpenMeteo(location, restTemplate);
//    }
//
//    private double[] getCoordsFromOSM(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OSM_API + "?q=" + encodedLoc + "&format=json&limit=1";
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            JSONArray array = new JSONArray(response.getBody());
//            if (array.length() > 0) {
//                JSONObject obj = array.getJSONObject(0);
//                return new double[]{obj.getDouble("lat"), obj.getDouble("lon")};
//            }
//        } catch (Exception e) {}
//        return null;
//    }
//
//    private double[] getCoordsFromOpenMeteo(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OPEN_METEO_API + "?name=" + encodedLoc + "&count=1&language=en&format=json";
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//            if (root.has("results")) {
//                JSONObject obj = root.getJSONArray("results").getJSONObject(0);
//                return new double[]{obj.getDouble("latitude"), obj.getDouble("longitude")};
//            }
//        } catch (Exception e) {}
//        return null;
//    }
//
//    private List<Map<String, String>> createErrorList(String name, String desc) {
//        List<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("name", name);
//        map.put("description", desc);
//        list.add(map);
//        return list;
//    }
//}








//package com.triplens.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//public class TouristSpotService {
//
//    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
//    private final String OPEN_METEO_API = "https://geocoding-api.open-meteo.com/v1/search";
//    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
//    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
//
//    public List<Map<String, String>> getNearbySpots(String location) {
//        RestTemplate restTemplate = new RestTemplate();
//        List<Map<String, String>> candidates = new ArrayList<>();
//
//        try {
//            // 1. Get Coordinates
//            double[] coords = getCoordinatesWithBackup(location, restTemplate);
//            if (coords == null) {
//                return createErrorList("Location Error", "Could not find coordinates.");
//            }
//
//            // 2. Fetch Data
//            String wikiUrl = WIKI_API + "?action=query&generator=geosearch" +
//                    "&ggscoord=" + coords[0] + "|" + coords[1] +
//                    "&ggsradius=10000" +     // 10km
//                    "&ggslimit=50" +         // Limit 50
//                    "&prop=pageimages|extracts|info" + 
//                    "&pithumbsize=600" +     
//                    "&exintro=true" +        
//                    "&explaintext=true" +    
//                    "&exsentences=2" +       
//                    "&format=json";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(wikiUrl, HttpMethod.GET, entity, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//
//            if (root.has("query") && root.getJSONObject("query").has("pages")) {
//                JSONObject pages = root.getJSONObject("query").getJSONObject("pages");
//                Iterator<String> keys = pages.keys();
//
//                while (keys.hasNext()) {
//                    JSONObject page = pages.getJSONObject(keys.next());
//                    String title = page.optString("title");
//                    String lowerTitle = title.toLowerCase();
//                    String extract = page.optString("extract", "").toLowerCase();
//
//                    // --- FILTER 1: Skip the Location Itself ---
//                    if (lowerTitle.equalsIgnoreCase(location.toLowerCase())) continue;
//
//                    // --- FILTER 2: Title Blockers (Hard Delete) ---
//                    if (lowerTitle.contains("district") || lowerTitle.contains("division")) continue;
//                    if (lowerTitle.contains("constituency") || lowerTitle.contains("lok sabha")) continue;
//                    if (lowerTitle.contains("assembly") || lowerTitle.contains("vidhan sabha")) continue;
//                    if (lowerTitle.contains("municipal") || lowerTitle.contains("corporation")) continue;
//                    if (lowerTitle.contains("headquarters") || lowerTitle.contains("cantonment")) continue;
//                    if (lowerTitle.contains("school") || lowerTitle.contains("college")) continue;
//                    if (lowerTitle.contains("university") || lowerTitle.contains("institute")) continue;
//                    if (lowerTitle.contains("station") || lowerTitle.contains("railway")) continue;
//                    if (lowerTitle.contains("metro") || lowerTitle.contains("airport")) continue;
//                    // NEW: Block Geography/Directions
//                    if (lowerTitle.contains("geography")) continue;
//                    if (lowerTitle.startsWith("north ") || lowerTitle.startsWith("south ")) continue; 
//                    if (lowerTitle.startsWith("east ") || lowerTitle.startsWith("west ")) continue;
//                    
//                    // --- FILTER 3: Context Blockers (Read the Description) ---
//                    // This kills "Bowbazar" and "Entally"
//                    if (extract.contains("neighbourhood") || extract.contains("neighborhood")) continue;
//                    if (extract.contains("locality") || extract.contains("residential")) continue;
//                    if (extract.contains("census town") || extract.contains("administrative")) continue;
//                    if (extract.contains("is a taluka") || extract.contains("is a town")) continue;
//                    
//                    // --- SCORING SYSTEM ---
//                    int score = page.optInt("length", 0); 
//
//                    // Image Bonus
//                    if (page.has("thumbnail")) score += 50000;
//
//                    // Keyword Boosts (Make the famous stuff float to top)
//                    if (lowerTitle.contains("museum")) score += 500000; // Asutosh/Indian Museum
//                    if (lowerTitle.contains("memorial")) score += 500000; // Victoria Memorial
//                    if (lowerTitle.contains("bridge")) score += 500000; // Howrah Bridge
//                    if (lowerTitle.contains("temple") || lowerTitle.contains("mandir")) score += 300000;
//                    if (lowerTitle.contains("fort")) score += 300000;
//                    if (lowerTitle.contains("palace")) score += 300000;
//                    if (lowerTitle.contains("garden")) score += 300000;
//                    if (lowerTitle.contains("market")) score += 200000; // New Market
//                    if (lowerTitle.contains("lake")) score += 200000;
//                    if (lowerTitle.contains("church") || lowerTitle.contains("cathedral")) score += 200000;
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", title);
//                    map.put("description", page.optString("extract", "A famous tourist destination."));
//                    map.put("distance_km", "In " + location);
//                    map.put("type", "Top Attraction");
//                    
//                    if (page.has("thumbnail")) {
//                        map.put("image_url", page.getJSONObject("thumbnail").optString("source"));
//                    } else {
//                        map.put("image_url", "https://via.placeholder.com/400x300?text=No+Image");
//                    }
//                    map.put("score", String.valueOf(score)); 
//                    candidates.add(map);
//                }
//            }
//
//            // 3. Sort by Score
//            candidates.sort((a, b) -> Integer.parseInt(b.get("score")) - Integer.parseInt(a.get("score")));
//
//            // 4. Filter Duplicates & Return Top 5
//            List<Map<String, String>> finalResult = new ArrayList<>();
//            for (Map<String, String> candidate : candidates) {
//                if (finalResult.size() >= 5) break;
//
//                String newName = candidate.get("name").toLowerCase();
//                boolean isDuplicate = false;
//
//                for (Map<String, String> existing : finalResult) {
//                    String existingName = existing.get("name").toLowerCase();
//                    if (newName.contains(existingName) || existingName.contains(newName)) {
//                        isDuplicate = true;
//                        break;
//                    }
//                }
//
//                if (!isDuplicate) {
//                    candidate.remove("score");
//                    finalResult.add(candidate);
//                }
//            }
//            
//            return finalResult.isEmpty() ? createErrorList("No Data", "Wiki returned no results.") : finalResult;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createErrorList("Error", e.getMessage());
//        }
//    }
//
//    private double[] getCoordinatesWithBackup(String location, RestTemplate restTemplate) {
//        double[] coords = getCoordsFromOSM(location, restTemplate);
//        if (coords != null) return coords;
//        return getCoordsFromOpenMeteo(location, restTemplate);
//    }
//
//    private double[] getCoordsFromOSM(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OSM_API + "?q=" + encodedLoc + "&format=json&limit=1";
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            JSONArray array = new JSONArray(response.getBody());
//            if (array.length() > 0) {
//                JSONObject obj = array.getJSONObject(0);
//                return new double[]{obj.getDouble("lat"), obj.getDouble("lon")};
//            }
//        } catch (Exception e) {}
//        return null;
//    }
//
//    private double[] getCoordsFromOpenMeteo(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OPEN_METEO_API + "?name=" + encodedLoc + "&count=1&language=en&format=json";
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//            if (root.has("results")) {
//                JSONObject obj = root.getJSONArray("results").getJSONObject(0);
//                return new double[]{obj.getDouble("latitude"), obj.getDouble("longitude")};
//            }
//        } catch (Exception e) {}
//        return null;
//    }
//
//    private List<Map<String, String>> createErrorList(String name, String desc) {
//        List<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("name", name);
//        map.put("description", desc);
//        list.add(map);
//        return list;
//    }
//}








//package com.triplens.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//public class TouristSpotService {
//
//    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
//    private final String OPEN_METEO_API = "https://geocoding-api.open-meteo.com/v1/search";
//    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
//    // Using a Browser User-Agent is more reliable for free APIs
//    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
//
//    public List<Map<String, String>> getNearbySpots(String location) {
//        RestTemplate restTemplate = new RestTemplate();
//        List<Map<String, String>> candidates = new ArrayList<>();
//
//        try {
//            // 1. Get Coordinates
//            double[] coords = getCoordinatesWithBackup(location, restTemplate);
//            if (coords == null) {
//                return createErrorList("Location Error", "Could not find coordinates.");
//            }
//
//            System.out.println("DEBUG: Searching near " + coords[0] + ", " + coords[1]);
//
//            // 2. Fetch Data (Reduced limit to 50 for stability)
//            String wikiUrl = WIKI_API + "?action=query&generator=geosearch" +
//                    "&ggscoord=" + coords[0] + "|" + coords[1] +
//                    "&ggsradius=10000" +     // 10km Radius
//                    "&ggslimit=50" +         // LIMIT 50: Prevents API Timeouts
//                    "&prop=pageimages|extracts|info" + 
//                    "&pithumbsize=600" +     
//                    "&exintro=true" +        
//                    "&explaintext=true" +    
//                    "&exsentences=2" +       
//                    "&format=json";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(wikiUrl, HttpMethod.GET, entity, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//
//            if (root.has("query") && root.getJSONObject("query").has("pages")) {
//                JSONObject pages = root.getJSONObject("query").getJSONObject("pages");
//                Iterator<String> keys = pages.keys();
//
//                while (keys.hasNext()) {
//                    JSONObject page = pages.getJSONObject(keys.next());
//                    String title = page.optString("title");
//                    String lowerTitle = title.toLowerCase();
//
//                    // Skip the city itself
//                    if (lowerTitle.equalsIgnoreCase(location.toLowerCase())) continue;
//
//                    // --- SCORING LOGIC ---
//                    int score = page.optInt("length", 0); 
//
//                    // 1. Image Check (Bonus, not a filter)
//                    // We prefer spots with images, but don't delete ones without them instantly
//                    if (page.has("thumbnail")) {
//                        score += 50000; 
//                    }
//
//                    // 2. Keywords (Balanced)
//                    if (lowerTitle.contains("fort")) score += 200000;
//                    if (lowerTitle.contains("temple") || lowerTitle.contains("mandir")) score += 200000;
//                    if (lowerTitle.contains("taj") || lowerTitle.contains("tomb")) score += 250000; // Specific boost for Taj/Tombs
//                    if (lowerTitle.contains("museum")) score += 150000;
//                    if (lowerTitle.contains("palace")) score += 150000;
//                    if (lowerTitle.contains("garden") || lowerTitle.contains("park")) score += 100000;
//                    if (lowerTitle.contains("gate")) score += 150000; 
//                    if (lowerTitle.contains("minar")) score += 150000;
//                    if (lowerTitle.contains("memorial")) score += 100000; // Kept high, but not dominating
//                    if (lowerTitle.contains("market")) score += 100000;
//                    if (lowerTitle.contains("church")) score += 100000;
//
//                    // 3. Penalties (Bury boring stuff)
//                    if (lowerTitle.contains("school") || lowerTitle.contains("college")) score -= 500000;
//                    if (lowerTitle.contains("metro") || lowerTitle.contains("station")) score -= 500000;
//                    if (lowerTitle.contains("hospital")) score -= 500000;
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", title);
//                    map.put("description", page.optString("extract", "A famous tourist destination."));
//                    map.put("distance_km", "In " + location);
//                    map.put("type", "Top Attraction");
//                    
//                    if (page.has("thumbnail")) {
//                        map.put("image_url", page.getJSONObject("thumbnail").optString("source"));
//                    } else {
//                        map.put("image_url", "https://via.placeholder.com/400x300?text=No+Image");
//                    }
//                    
//                    map.put("score", String.valueOf(score)); 
//                    candidates.add(map);
//                }
//            }
//
//            // 3. Sort by Score
//            candidates.sort((a, b) -> Integer.parseInt(b.get("score")) - Integer.parseInt(a.get("score")));
//
//            // 4. Return Top 5
//            List<Map<String, String>> finalResult = new ArrayList<>();
//            for (int i = 0; i < Math.min(5, candidates.size()); i++) {
//                Map<String, String> spot = candidates.get(i);
//                spot.remove("score"); 
//                finalResult.add(spot);
//            }
//            
//            return finalResult.isEmpty() ? createErrorList("No Data", "Wiki returned no results.") : finalResult;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createErrorList("Error", e.getMessage());
//        }
//    }
//
//    private double[] getCoordinatesWithBackup(String location, RestTemplate restTemplate) {
//        double[] coords = getCoordsFromOSM(location, restTemplate);
//        if (coords != null) return coords;
//        return getCoordsFromOpenMeteo(location, restTemplate);
//    }
//
//    private double[] getCoordsFromOSM(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OSM_API + "?q=" + encodedLoc + "&format=json&limit=1";
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            JSONArray array = new JSONArray(response.getBody());
//            if (array.length() > 0) {
//                JSONObject obj = array.getJSONObject(0);
//                return new double[]{obj.getDouble("lat"), obj.getDouble("lon")};
//            }
//        } catch (Exception e) {}
//        return null;
//    }
//
//    private double[] getCoordsFromOpenMeteo(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OPEN_METEO_API + "?name=" + encodedLoc + "&count=1&language=en&format=json";
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//            if (root.has("results")) {
//                JSONObject obj = root.getJSONArray("results").getJSONObject(0);
//                return new double[]{obj.getDouble("latitude"), obj.getDouble("longitude")};
//            }
//        } catch (Exception e) {}
//        return null;
//    }
//
//    private List<Map<String, String>> createErrorList(String name, String desc) {
//        List<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("name", name);
//        map.put("description", desc);
//        list.add(map);
//        return list;
//    }
//}




//package com.triplens.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//public class TouristSpotService {
//
//    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
//    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
//    
//    // FAKE USER AGENT (Crucial for Wikipedia/OSM access)
//    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
//
//    public List<Map<String, String>> getNearbySpots(String location) {
//        RestTemplate restTemplate = new RestTemplate();
//        List<Map<String, String>> result = new ArrayList<>();
//
//        try {
//            // STEP 1: Get Coordinates
//            double[] coords = getCoordinates(location, restTemplate);
//            if (coords == null) {
//                return createErrorList("Location Not Found", "OSM returned 0 results.");
//            }
//            double lat = coords[0];
//            double lon = coords[1];
//
//            // STEP 2: Advanced Wikipedia Query
//            // generator=geosearch: Find pages near location
//            // prop=pageimages|extracts: Get the main image and a short summary
//            // piprop=thumbnail: Get the thumbnail URL
//            // pilimit=50: Check up to 50 pages to find good ones
//            String wikiUrl = WIKI_API + "?action=query&generator=geosearch" +
//                    "&ggscoord=" + lat + "|" + lon +
//                    "&ggsradius=10000" +     // 10km Radius
//                    "&ggslimit=50" +         // Fetch 50 candidates (we will filter top 10)
//                    "&prop=pageimages|coordinates|info" + // Get images to check quality
//                    "&pithumbsize=500" +
//                    "&format=json";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(wikiUrl, HttpMethod.GET, entity, String.class);
//            JSONObject root = new JSONObject(response.getBody());
//
//            if (root.has("query") && root.getJSONObject("query").has("pages")) {
//                JSONObject pages = root.getJSONObject("query").getJSONObject("pages");
//                Iterator<String> keys = pages.keys();
//
//                while (keys.hasNext()) {
//                    String pageId = keys.next();
//                    JSONObject page = pages.getJSONObject(pageId);
//                    String title = page.optString("title");
//
//                    // FILTER 1: Skip the location itself (e.g., skip "Pune" if searching "Pune")
//                    if (title.equalsIgnoreCase(location)) continue;
//
//                    // FILTER 2: MUST HAVE AN IMAGE (The "Famous" Filter)
//                    // Metro stations and colleges often don't have high-quality page images.
//                    if (!page.has("thumbnail")) {
//                        continue; 
//                    }
//
//                    // FILTER 3: Block keywords for noise reduction
//                    String lowerTitle = title.toLowerCase();
//                    if (lowerTitle.contains("metro station") || 
//                        lowerTitle.contains("airport") || 
//                        lowerTitle.contains("college") || 
//                        lowerTitle.contains("school")) {
//                        continue;
//                    }
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", title);
//                    map.put("description", "A famous landmark in " + location); // Wiki extracts are often too long/messy
//                    
//                    // Calculate distance manually if needed, or just mark as "Nearby"
//                    // (Wikipedia's generator doesn't return distance directly like the list query did, 
//                    // but the quality is better. We'll mark it generic for now.)
//                    map.put("distance_km", "Nearby"); 
//                    map.put("type", "Tourist Attraction");
//                    map.put("image_url", page.getJSONObject("thumbnail").optString("source"));
//
//                    result.add(map);
//
//                    // Limit to top 10 results
//                    if (result.size() >= 10) break;
//                }
//            }
//
//            if (result.isEmpty()) {
//                // Fallback if strict filtering removed everything
//                return createErrorList("No Major Spots Found", "Try increasing the range or searching a major city.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createErrorList("System Error", e.getMessage());
//        }
//
//        return result;
//    }
//
//    private double[] getCoordinates(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OSM_API + "?q=" + encodedLoc + "&format=json&limit=1";
//            
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            JSONArray array = new JSONArray(response.getBody());
//
//            if (array.length() > 0) {
//                JSONObject obj = array.getJSONObject(0);
//                return new double[]{obj.getDouble("lat"), obj.getDouble("lon")};
//            }
//        } catch (Exception e) {
//            System.err.println("Geocoding failed: " + e.getMessage());
//        }
//        return null;
//    }
//
//    private List<Map<String, String>> createErrorList(String name, String desc) {
//        List<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("name", name);
//        map.put("description", desc);
//        list.add(map);
//        return list;
//    }
//}












//package com.triplens.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//public class TouristSpotService {
//
//    private final String OSM_API = "https://nominatim.openstreetmap.org/search";
//    private final String WIKI_API = "https://en.wikipedia.org/w/api.php";
//
//    // FAKE USER AGENT (Looks like Chrome to bypass blocks)
//    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
//
//    public List<Map<String, String>> getNearbySpots(String location) {
//        RestTemplate restTemplate = new RestTemplate();
//        List<Map<String, String>> result = new ArrayList<>();
//
//        try {
//            // STEP 1: Get Coordinates
//            double[] coords = getCoordinates(location, restTemplate);
//            
//            if (coords == null) {
//                return createErrorList("Location Not Found", "OSM returned 0 results for: " + location);
//            }
//
//            double lat = coords[0];
//            double lon = coords[1];
//
//            // STEP 2: Search Wikipedia
//            String wikiUrl = WIKI_API + "?action=query&list=geosearch" +
//                    "&gscoord=" + lat + "|" + lon +
//                    "&gsradius=10000" +  // 10km Radius
//                    "&gslimit=10" +      
//                    "&format=json";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(wikiUrl, HttpMethod.GET, entity, String.class);
//            
//            JSONObject root = new JSONObject(response.getBody());
//            
//            if (root.has("query") && root.getJSONObject("query").has("geosearch")) {
//                JSONArray spots = root.getJSONObject("query").getJSONArray("geosearch");
//
//                for (int i = 0; i < spots.length(); i++) {
//                    JSONObject spot = spots.getJSONObject(i);
//                    String title = spot.getString("title");
//
//                    if (title.equalsIgnoreCase(location)) continue;
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", title);
//                    map.put("distance_km", String.format("%.2f km", spot.getDouble("dist") / 1000));
//                    map.put("type", "Famous Landmark");
//                    map.put("description", "Famous spot found near " + location);
//                    result.add(map);
//                }
//            }
//
//            if (result.isEmpty()) {
//                return createErrorList("No Famous Spots Found", "Wikipedia found no articles near these coordinates.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createErrorList("System Error", e.getMessage());
//        }
//
//        return result;
//    }
//
//    private double[] getCoordinates(String location, RestTemplate restTemplate) {
//        try {
//            String encodedLoc = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            String url = OSM_API + "?q=" + encodedLoc + "&format=json&limit=1";
//
//            // LOGGING THE URL TO DEBUG
//            System.out.println("Calling OSM URL: " + url);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", USER_AGENT); // Use the "Chrome" User-Agent
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            
//            // LOGGING THE RESPONSE
//            System.out.println("OSM Response: " + response.getBody());
//
//            JSONArray array = new JSONArray(response.getBody());
//
//            if (array.length() > 0) {
//                JSONObject obj = array.getJSONObject(0);
//                return new double[]{obj.getDouble("lat"), obj.getDouble("lon")};
//            }
//        } catch (Exception e) {
//            System.err.println("Geocoding failed: " + e.getMessage());
//        }
//        return null;
//    }
//
//    private List<Map<String, String>> createErrorList(String name, String desc) {
//        List<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("name", name);
//        map.put("description", desc);
//        list.add(map);
//        return list;
//    }
//}










//package com.triplens.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//public class TouristSpotService {
//
//    private final String API_URL = "https://nominatim.openstreetmap.org/search";
//
//    public List<Map<String, String>> getNearbySpots(String location) {
//        RestTemplate restTemplate = new RestTemplate();
//        List<Map<String, String>> result = new ArrayList<>();
//
//        try {
//            // 1. Encode the location (Fixes "New Delhi" or "Taj Mahal" space issues)
//            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
//            
//            // 2. Build URL: We use "tourism" instead of "tourist attraction" as it is broader
//            String url = API_URL + "?q=tourism+in+" + encodedLocation + "&format=json&limit=5";
//
//            // --- DEBUG LOG: Click this link in your console to see what OSM returns! ---
//            System.out.println("Testing URL: " + url);
//            // ---------------------------------------------------------------------------
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("User-Agent", "TripLens-StudentProject/1.0"); // Required by OSM
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                url, 
//                HttpMethod.GET, 
//                entity, 
//                String.class
//            );
//
//            JSONArray array = new JSONArray(response.getBody());
//
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = array.getJSONObject(i);
//                Map<String, String> map = new HashMap<>();
//                
//                String placeName = obj.optString("display_name", "Unknown Place");
//                // Clean up long names
//                if (placeName.contains(",")) {
//                    placeName = placeName.split(",")[0]; 
//                }
//
//                map.put("name", placeName);
//                map.put("distance_km", "Near Center"); 
//                map.put("type", obj.optString("type", "attraction"));
//                map.put("description", "A popular destination in " + location);
//
//                result.add(map);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Map<String, String> errorMap = new HashMap<>();
//            errorMap.put("name", "Error");
//            errorMap.put("description", "Failed: " + e.getMessage());
//            result.add(errorMap);
//        }
//
//        return result;
//    }
//}