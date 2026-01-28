package com.org.Triplens.Services.StateExtractor;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class WikipediaStateFestivalLinkScraper {

    private static final String BASE_URL = "https://en.wikipedia.org";

    public Set<String> getFestivalLinksFromState(String stateCategoryUrl) throws Exception {

        Set<String> links = new HashSet<>();

        Document doc = Jsoup.connect(stateCategoryUrl)
                .userAgent("Mozilla/5.0")
                .get();

        Elements pages = doc.select("#mw-pages a[href^=/wiki/]");

        for (org.jsoup.nodes.Element a : pages) {
            String href = a.attr("href");

            if (!href.contains(":") && !href.contains("Category")) {
                links.add(BASE_URL + href);
            }
        }
        return links;
    }
}

