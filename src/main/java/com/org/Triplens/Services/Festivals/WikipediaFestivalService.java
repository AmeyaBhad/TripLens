package com.org.Triplens.Services.Festivals;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.org.Triplens.DTO.FestivalDTO;

@Service
public class WikipediaFestivalService {

    /**
     * Scrapes a SINGLE festival Wikipedia page
     * Used by StateFestivalIngestionService
     */
    public FestivalDTO scrapeFestivalPagePublic(String url) {
        return scrapeFestivalPage(url);
    }

    // ---------------- PRIVATE CORE SCRAPER ----------------

    private FestivalDTO scrapeFestivalPage(String url) {

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            String name = extractName(doc);
            String description = extractDescription(doc);
            String month = extractMonth(doc);
            List<String> categories = extractCategories(doc);

            // Reject non-festival pages (extra safety)
            if (!isFestivalPage(categories)) {
                return null;
            }

            FestivalDTO dto = new FestivalDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setMonth(month);
            dto.setCategories(categories);
            dto.setWikipediaUrl(url);

            return dto;

        } catch (Exception e) {
            return null;
        }
    }

    // ---------------- FIELD EXTRACTION ----------------

    private String extractName(Document doc) {
        Element h1 = doc.selectFirst("h1");
        return h1 != null ? h1.text() : null;
    }

    private String extractDescription(Document doc) {
        for (Element p : doc.select("p")) {
            String text = p.text();
            if (text.length() > 120 && !text.contains("may refer to")) {
                return text;
            }
        }
        return "Description not available";
    }

    private String extractMonth(Document doc) {
        for (Element row : doc.select("table.infobox tr")) {
            String header = row.select("th").text().toLowerCase();
            if (header.contains("date") || header.contains("month")) {
                return row.select("td").text();
            }
        }
        return "Varies";
    }

    private List<String> extractCategories(Document doc) {
        List<String> categories = new ArrayList<>();
        for (Element e : doc.select("#mw-normal-catlinks li a")) {
            categories.add(e.text());
        }
        return categories;
    }

    // ---------------- SAFETY FILTER ----------------

    private boolean isFestivalPage(List<String> categories) {
        for (String c : categories) {
            String lower = c.toLowerCase();
            if (lower.contains("festival") ||
                lower.contains("hindu") ||
                lower.contains("sikh") ||
                lower.contains("jain") ||
                lower.contains("buddhist")) {
                return true;
            }
        }
        return false;
    }
}
