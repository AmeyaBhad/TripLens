package com.org.Triplens.Services.StateExtractor;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class WikipediaStateCategoryScraper {

    private static final String BASE_URL = "https://en.wikipedia.org";
    private static final String CATEGORY_URL =
        BASE_URL + "/wiki/Category:Festivals_in_India_by_state_or_union_territory";

    public Map<String, String> getStateCategoryLinks() throws Exception {

        Map<String, String> stateCategories = new HashMap<>();

        Document doc = Jsoup.connect(CATEGORY_URL)
                .userAgent("Mozilla/5.0")
                .get();

        Elements links = doc.select("#mw-subcategories a[href^=/wiki/Category:]");

        for (Element link : links) {
            String name = link.text(); // Festivals in Maharashtra
            String href = link.attr("href");

            if (name.startsWith("Festivals in")) {
                String state = name.replace("Festivals in", "").trim();
                stateCategories.put(state, BASE_URL + href);
            }
        }
        return stateCategories;
    }
}

