package com.org.Triplens.Services.Festivals;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Triplens.DAO.FestivalDao;
import com.org.Triplens.DTO.FestivalDTO;
import com.org.Triplens.Services.StateExtractor.WikipediaStateCategoryScraper;
import com.org.Triplens.Services.StateExtractor.WikipediaStateFestivalLinkScraper;
import com.org.Triplens.entity.FestivalEntity;

@Service
public class StateFestivalIngestionService {

    @Autowired private WikipediaStateCategoryScraper categoryScraper;
    @Autowired private WikipediaStateFestivalLinkScraper linkScraper;
    @Autowired private WikipediaFestivalService festivalScraper;
    @Autowired private FestivalDao festivalDao;

    public int ingestAllStateFestivals() throws Exception {

        Map<String, String> stateCategories =
                categoryScraper.getStateCategoryLinks();

        int count = 0;

        for (String state : stateCategories.keySet()) {

            String categoryUrl = stateCategories.get(state);
            Set<String> festivalLinks =
                    linkScraper.getFestivalLinksFromState(categoryUrl);

            for (String link : festivalLinks) {
                FestivalDTO dto = festivalScraper.scrapeFestivalPagePublic(link);
                if (dto == null) continue;

                if (festivalDao.festivalExists(dto.getName(), state)) continue;

                FestivalEntity f = new FestivalEntity();
                f.setName(dto.getName());
                f.setDescription(dto.getDescription());
                f.setMonth(dto.getMonth());
                f.setState(state);
                f.setCategories(dto.getCategories());
                f.setWikipediaUrl(dto.getWikipediaUrl());

                festivalDao.saveFestival(f);
                count++;
                Thread.sleep(200);
            }
        }
        return count;
    }
}
