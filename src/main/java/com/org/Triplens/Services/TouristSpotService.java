package com.org.Triplens.Services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public List<Map<String, String>> getNearbySpots(String location) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Check for City-Specific "Gold List" (Direct Title Fetch)
        List<String> specificTitles = getCitySpecificKeywords(location);
        if (!specificTitles.isEmpty()) {
            return fetchSpotsByTitles(specificTitles, location, restTemplate);
        }

        List<Map<String, String>> candidates = new ArrayList<>();
        List<String> priorityKeywords = new ArrayList<>(); // Empty for geosearch if not supported city

        try {
            double[] coords = getCoordinatesWithBackup(location, restTemplate);
            if (coords == null) {
                return createErrorList("Location Error", "Could not find coordinates.");
            }

            String wikiUrl = WIKI_API +
                    "?action=query" +
                    "&generator=geosearch" +
                    "&ggscoord=" + coords[0] + "|" + coords[1] +
                    "&ggsradius=10000" +
                    "&ggslimit=50" +
                    "&prop=pageimages|extracts|info|coordinates" +
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

                    if (lowerTitle.equalsIgnoreCase(location.toLowerCase()))
                        continue;

                    // ---- HARD FILTERS ----
                    if (lowerTitle.contains("district") || lowerTitle.contains("division"))
                        continue;
                    if (lowerTitle.contains("constituency") || lowerTitle.contains("lok sabha"))
                        continue;
                    if (lowerTitle.contains("assembly") || lowerTitle.contains("vidhan sabha"))
                        continue;
                    if (lowerTitle.contains("municipal") || lowerTitle.contains("corporation"))
                        continue;
                    if (lowerTitle.contains("headquarters") || lowerTitle.contains("cantonment"))
                        continue;
                    if (lowerTitle.contains("school") || lowerTitle.contains("college"))
                        continue;
                    if (lowerTitle.contains("university") || lowerTitle.contains("institute"))
                        continue;
                    if (lowerTitle.contains("hospital") || lowerTitle.contains("court"))
                        continue;
                    if (lowerTitle.contains("station") || lowerTitle.contains("railway"))
                        continue;
                    if (lowerTitle.contains("metro") || lowerTitle.contains("airport"))
                        continue;
                    if (lowerTitle.contains("geography"))
                        continue;

                    if (extract.contains("is a taluka") || extract.contains("is a town"))
                        continue;
                    if (extract.contains("census town") || extract.contains("administrative"))
                        continue;

                    int score = page.optInt("length", 0);

                    // 🔧 REQUIRED FIX: normalize smart quotes
                    String normalizedTitle = lowerTitle.replace("’", "'");

                    // ---- GOLD LIST BOOST ----
                    for (String keyword : priorityKeywords) {
                        String normalizedKeyword = keyword.replace("’", "'");
                        if (normalizedTitle.contains(normalizedKeyword)) {
                            score += 10_000_000;
                            break;
                        }
                    }

                    if (page.has("thumbnail"))
                        score += 50_000;

                    if (lowerTitle.contains("fort"))
                        score += 200_000;
                    if (lowerTitle.contains("temple") || lowerTitle.contains("mandir"))
                        score += 200_000;
                    if (lowerTitle.contains("museum"))
                        score += 150_000;
                    if (lowerTitle.contains("palace"))
                        score += 150_000;
                    if (lowerTitle.contains("garden") || lowerTitle.contains("park"))
                        score += 100_000;
                    if (lowerTitle.contains("market"))
                        score += 100_000;
                    if (lowerTitle.contains("lake"))
                        score += 100_000;

                    Map<String, String> map = new HashMap<>();
                    // Fix: Add Coordinates
                    if (page.has("coordinates")) {
                        JSONArray coordArray = page.getJSONArray("coordinates");
                        if (coordArray.length() > 0) {
                            JSONObject coord = coordArray.getJSONObject(0);
                            map.put("lat", String.valueOf(coord.getDouble("lat")));
                            map.put("lon", String.valueOf(coord.getDouble("lon")));
                        }
                    }
                    map.put("name", title);
                    map.put("description", page.optString("extract", "A famous tourist destination."));
                    map.put("distance_km", "In " + location);
                    map.put("type", "Top Attraction");
                    map.put("score", String.valueOf(score));

                    if (page.has("thumbnail")) {
                        map.put("image_url",
                                page.getJSONObject("thumbnail").optString("source"));
                        candidates.add(map);
                    }
                    // else: Skip spots without images as per user request
                }
            }

            candidates.sort(
                    (a, b) -> Integer.parseInt(b.get("score")) - Integer.parseInt(a.get("score")));

            List<Map<String, String>> finalResult = new ArrayList<>();
            for (Map<String, String> candidate : candidates) {
                if (finalResult.size() >= 5)
                    break;

                String newName = candidate.get("name").toLowerCase();
                boolean duplicate = false;

                for (Map<String, String> existing : finalResult) {
                    String existingName = existing.get("name").toLowerCase();
                    if (newName.contains(existingName) || existingName.contains(newName)) {
                        duplicate = true;
                        break;
                    }
                }

                if (!duplicate) {
                    candidate.remove("score");
                    finalResult.add(candidate);
                }
            }

            return finalResult.isEmpty()
                    ? createErrorList("No Data", "Wiki returned no results.")
                    : finalResult;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorList("Error", e.getMessage());
        }
    }

    // --- DIRECT TITLE FETCH FOR SUPPORTED CITIES ---
    private List<Map<String, String>> fetchSpotsByTitles(List<String> titles, String location,
            RestTemplate restTemplate) {
        try {
            // Wikipedia expects checks pipe separated titles.
            // We must encode EACH title individually, then join with "|".
            // We must NOT encode the "|" separator itself.
            List<String> encodedBatch = new ArrayList<>();
            for (String t : titles) {
                encodedBatch.add(URLEncoder.encode(t, StandardCharsets.UTF_8));
            }
            String finalTitles = String.join("|", encodedBatch);

            String wikiUrl = WIKI_API +
                    "?action=query" +
                    "&titles=" + finalTitles +
                    "&redirects=1" +
                    "&prop=pageimages|extracts|info|coordinates" +
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
            List<Map<String, String>> results = new ArrayList<>();

            if (root.has("query") && root.getJSONObject("query").has("pages")) {
                JSONObject pages = root.getJSONObject("query").getJSONObject("pages");
                Iterator<String> keys = pages.keys();

                while (keys.hasNext()) {
                    JSONObject page = pages.getJSONObject(keys.next());
                    if (page.has("missing"))
                        continue;

                    String title = page.optString("title");
                    Map<String, String> map = new HashMap<>();
                    if (page.has("coordinates")) {
                        JSONArray coords = page.getJSONArray("coordinates");
                        if (coords.length() > 0) {
                            JSONObject coord = coords.getJSONObject(0);
                            map.put("lat", String.valueOf(coord.getDouble("lat")));
                            map.put("lon", String.valueOf(coord.getDouble("lon")));
                        }
                    }
                    map.put("name", title);
                    map.put("description", page.optString("extract", "A popular tourist attraction in " + location));
                    map.put("distance_km", "Famous Spot in " + location);
                    map.put("type", "Top Attraction");

                    if (page.has("thumbnail")) {
                        map.put("image_url", page.getJSONObject("thumbnail").optString("source"));
                        results.add(map);
                    }
                    // else: Skip spots without images as per user request
                }
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Fallback to geosearch on error
        }
    }

    // -------- COORDINATES --------

    private double[] getCoordinatesWithBackup(String location, RestTemplate restTemplate) {
        double[] coords = getCoordsFromOSM(location, restTemplate);
        return coords != null ? coords : getCoordsFromOpenMeteo(location, restTemplate);
    }

    private double[] getCoordsFromOSM(String location, RestTemplate restTemplate) {
        try {
            String encoded = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = OSM_API + "?q=" + encoded + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONArray array = new JSONArray(response.getBody());
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                return new double[] {
                        obj.getDouble("lat"),
                        obj.getDouble("lon")
                };
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private double[] getCoordsFromOpenMeteo(String location, RestTemplate restTemplate) {
        try {
            String encoded = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = OPEN_METEO_API +
                    "?name=" + encoded +
                    "&count=1&language=en&format=json";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            JSONObject root = new JSONObject(response.getBody());
            if (root.has("results")) {
                JSONObject obj = root.getJSONArray("results").getJSONObject(0);
                return new double[] {
                        obj.getDouble("latitude"),
                        obj.getDouble("longitude")
                };
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private List<Map<String, String>> createErrorList(String name, String desc) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", desc);
        return Collections.singletonList(map);
    }

    private List<String> getCitySpecificKeywords(String city) {
        String c = city.toLowerCase().trim();
        List<String> keywords = new ArrayList<>();

        if (c.contains("agra")) {
            keywords.addAll(Arrays.asList("Taj Mahal", "Agra Fort", "Mehtab Bagh", "Tomb of Itimad-ud-Daulah",
                    "Fatehpur Sikri", "Jama Masjid, Agra", "Akbar's tomb", "Mariam-uz-Zamani",
                    "Chini Ka Rauza", "Ram Bagh, Agra"));
        } else if (c.contains("jaipur")) {
            keywords.addAll(Arrays.asList("Amer Fort", "City Palace, Jaipur", "Hawa Mahal", "Jantar Mantar, Jaipur",
                    "Nahargarh Fort", "Jaigarh Fort", "Jal Mahal", "Albert Hall Museum", "Birla Mandir, Jaipur",
                    "Patrika Gate"));
        } else if (c.contains("delhi") || c.contains("new delhi")) {
            keywords.addAll(Arrays.asList("India Gate", "Qutb Minar", "Red Fort", "Humayun's Tomb", "Lotus Temple",
                    "Jama Masjid, Delhi", "Akshardham (Delhi)", "Rashtrapati Bhavan", "Lodhi Gardens",
                    "Chandni Chowk"));
        } else if (c.contains("mumbai")) {
            keywords.addAll(
                    Arrays.asList("Gateway of India",
                            "Marine Drive, Mumbai",
                            "Elephanta Caves",
                            "Chhatrapati Shivaji Maharaj Terminus",
                            "Juhu Beach",
                            "Haji Ali Dargah",
                            "Siddhivinayak Temple, Mumbai",
                            "Bandra-Worli Sea Link",
                            "Sanjay Gandhi National Park",
                            "Colaba Causeway"));
        } else if (c.contains("kolkata") || c.contains("calcutta")) {
            keywords.addAll(Arrays.asList(
                    "Victoria Memorial, Kolkata", "Howrah Bridge", "Dakshineswar Kali Temple",
                    "Kalighat Kali Temple", "Indian Museum, Kolkata", "Eden Gardens",
                    "Marble Palace, Kolkata", "Belur Math", "Science City, Kolkata", "Prinsep Ghat"));
        } else if (c.contains("bangalore") || c.contains("bengaluru")) {
            keywords.addAll(Arrays.asList(
                    "Lalbagh Botanical Garden", "Cubbon Park", "Bangalore Palace",
                    "ISKCON Temple Bangalore", "Bannerghatta National Park",
                    "Vidhana Soudha", "Nandi Hills", "Commercial Street",
                    "Ulsoor Lake", "Wonderla"));
        } else if (c.contains("hyderabad")) {
            keywords.addAll(Arrays.asList(
                    "Charminar", "Golconda Fort", "Hussain Sagar",
                    "Ramoji Film City", "Chowmahalla Palace", "Birla Mandir, Hyderabad",
                    "Salar Jung Museum", "Qutb Shahi Tombs",
                    "Necklace Road", "Mecca Masjid, Hyderabad"));
        } else if (c.contains("chennai") || c.contains("madras")) {
            keywords.addAll(Arrays.asList(
                    "Marina Beach", "Kapaleeshwarar Temple", "Fort St. George, India",
                    "Guindy National Park", "San Thome Basilica",
                    "Edward Elliot's Beach", "Government Museum, Chennai",
                    "Vivekanandar Illam", "DakshinaChitra", "Besant Nagar Beach"));
        } else if (c.contains("ahmedabad")) {
            keywords.addAll(Arrays.asList(
                    "Sabarmati Ashram", "Kankaria Lake", "Adalaj Stepwell",
                    "Sidi Saiyyed Mosque", "Jama Masjid, Ahmedabad",
                    "Sabarmati Riverfront", "Hutheesing Jain Temple",
                    "Auto World Vintage Car Museum",
                    "Calico Museum of Textiles", "Science City, Ahmedabad"));
        } else if (c.contains("pune")) {
            keywords.addAll(Arrays.asList(
                    "Shaniwar Wada", "Aga Khan Palace", "Sinhagad",
                    "Dagdusheth Halwai Ganpati Temple", "Pataleshwar Caves",
                    "Osho International Meditation Resort", "Parvati Hill", "Khadakwasla Dam",
                    "Rajiv Gandhi Zoological Park", "Pu La Deshpande Garden"));
        } else if (c.contains("goa") || c.contains("panaji")) {
            keywords.addAll(Arrays.asList(
                    "Basilica of Bom Jesus", "Calangute Beach", "Baga Beach",
                    "Fort Aguada", "Dudhsagar Falls",
                    "Anjuna Beach", "Candolim Beach",
                    "Chapora Fort", "Miramar, Goa", "Se Cathedral"));
        } else if (c.contains("varanasi") || c.contains("kashi")) {
            keywords.addAll(Arrays.asList(
                    "kashi vishwanath temple", "dashashwamedh ghat",
                    "sarnath", "assi ghat", "manikarnika ghat",
                    "banaras hindu university", "ramnagar fort",
                    "tulsi manas mandir", "durga temple", "bharat mata temple"));
        } else if (c.contains("udaipur")) {
            keywords.addAll(Arrays.asList(
                    "city palace udaipur", "lake pichola", "jag mandir",
                    "saheliyon ki bari", "fateh sagar lake",
                    "jagdeep temple", "bagore ki haveli",
                    "sajjangarh monsoon palace",
                    "gulab bagh", "ahar cenotaphs"));
        } else if (c.contains("amritsar")) {
            keywords.addAll(Arrays.asList(
                    "golden temple", "jallianwala bagh", "wagah border",
                    "durgiana temple", "ram bagh garden",
                    "partition museum", "hall bazaar",
                    "gobindgarh fort", "maharaja ranjit singh museum",
                    "harike wetland"));
        } else if (c.contains("jodhpur")) {
            keywords.addAll(Arrays.asList(
                    "mehrangarh fort", "umaid bhawan palace", "jaswant thada",
                    "mandore gardens", "clock tower jodhpur",
                    "toorji ka jhalra", "rao jodha desert park",
                    "ghanta ghar", "balsamand lake", "kaylana lake"));
        } else if (c.contains("kerala") || c.contains("kochi")) {
            keywords.addAll(Arrays.asList(
                    "fort kochi", "mattancherry palace",
                    "chinese fishing nets", "marine drive kochi",
                    "jew town", "paradesi synagogue",
                    "bolgatty palace", "cherai beach",
                    "vypeen island", "hill palace museum"));
        } else if (c.contains("mysore") || c.contains("mysuru")) {
            keywords.addAll(Arrays.asList(
                    "mysore palace", "brindavan gardens",
                    "chamundi hills", "st philomena cathedral",
                    "mysore zoo", "krs dam",
                    "jayalakshmi vilas palace",
                    "railway museum mysore",
                    "karanji lake", "srirangapatna"));
        } else if (c.contains("rishikesh")) {
            keywords.addAll(Arrays.asList(
                    "laxman jhula", "ram jhula",
                    "triveni ghat", "parmarth niketan",
                    "neelkanth mahadev temple",
                    "beatles ashram", "rajaji national park",
                    "kaudiyala", "shivpuri", "bharat mandir"));
        } else if (c.contains("shimla")) {
            keywords.addAll(Arrays.asList(
                    "the ridge", "mall road shimla",
                    "jakhu temple", "christ church shimla",
                    "kufri", "viceregal lodge",
                    "green valley", "annandale",
                    "summer hill", "chadwick falls"));
        } else if (c.contains("manali")) {
            keywords.addAll(Arrays.asList(
                    "solang valley", "rohtang pass",
                    "hidimba devi temple", "jogini waterfall",
                    "old manali", "manu temple",
                    "vashisht hot springs",
                    "beas river", "naggar castle",
                    "hampta pass"));
        } else if (c.contains("darjeeling")) {
            keywords.addAll(Arrays.asList(
                    "tiger hill", "batasia loop",
                    "ghoom monastery", "darjeeling tea gardens",
                    "himalayan mountaineering institute",
                    "padmaja naidu zoo", "peace pagoda",
                    "darjeeling ropeway",
                    "observatory hill", "rock garden darjeeling"));
        } else if (c.contains("ooty")) {
            keywords.addAll(Arrays.asList(
                    "botanical garden ooty", "ooty lake",
                    "doddabetta peak", "rose garden ooty",
                    "emerald lake", "avalanche lake",
                    "nilgiri mountain railway",
                    "pine forest ooty", "pykara falls",
                    "deer park ooty"));
        } else if (c.contains("nashik")) {
            keywords.addAll(Arrays.asList(
                    "trimbakeshwar temple", "pandavleni caves",
                    "sula vineyards", "kalaram temple",
                    "panchavati", "ramkund",
                    "anjaneri hills", "harihar fort",
                    "york winery", "gangapur dam"));
        } else if (c.contains("visakhapatnam") || c.contains("vizag")) {
            keywords.addAll(Arrays.asList(
                    "rk beach", "submarine museum",
                    "kailasagiri hill", "rushikonda beach",
                    "yarada beach", "simhachalam temple",
                    "borra caves", "araku valley",
                    "indira gandhi zoological park",
                    "dolphin’s nose"));
        } else if (c.contains("lucknow")) {
            keywords.addAll(Arrays.asList(
                    "bara imambara", "chota imambara",
                    "rumi darwaza", "hazratganj",
                    "ambedkar memorial park", "residency lucknow",
                    "janeshwar mishra park", "clock tower lucknow",
                    "dilkusha kothi", "state museum lucknow"));
        } else if (c.contains("bhubaneswar") || c.contains("puri")) {
            keywords.addAll(Arrays.asList(
                    "jagannath temple puri", "konark sun temple",
                    "lingaraj temple", "udayagiri caves",
                    "khandagiri caves", "puri beach",
                    "chilika lake", "ramachandi beach",
                    "bindu sagar lake", "mukteswar temple"));
        } else if (c.contains("indore")) {
            keywords.addAll(Arrays.asList(
                    "rajwada palace", "sarafa bazaar",
                    "lal bagh palace", "khajrana ganesh temple",
                    "patalpani waterfall", "rala mandal sanctuary",
                    "central museum indore", "annapurna temple",
                    "chhappan dukaan", "gulawat lotus valley"));
        } else if (c.contains("patna")) {
            keywords.addAll(Arrays.asList(
                    "golghar", "takht sri patna sahib",
                    "buddha smriti park", "patna museum",
                    "mahatma gandhi setu", "sanjay gandhi biological park",
                    "eco park patna", "agham kuan",
                    "kumhrar archaeological park", "planetarium patna"));
        } else if (c.contains("bhopal")) {
            keywords.addAll(Arrays.asList(
                    "upper lake bhopal", "van vihar national park",
                    "sanchi stupas", "bhimbetka rock shelters",
                    "lower lake", "birla mandir bhopal",
                    "state museum bhopal", "tribal museum bhopal",
                    "taj-ul-masajid", "kerwa dam"));
        } else if (c.contains("surat")) {
            keywords.addAll(Arrays.asList(
                    "dumas beach", "dutch garden",
                    "sarthana nature park", "science centre surat",
                    "surat castle", "gopi talav",
                    "ambika niketan temple", "iskcon surat",
                    "jagdishchandra bose aquarium", "ubharat beach"));
        }

        return keywords;
    }
}