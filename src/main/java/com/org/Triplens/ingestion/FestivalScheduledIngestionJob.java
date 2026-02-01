package com.org.Triplens.ingestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.org.Triplens.Services.Festivals.StateFestivalIngestionService;

@Component
public class FestivalScheduledIngestionJob {

    @Autowired
    private StateFestivalIngestionService ingestionService;

    @Async
    @Scheduled(cron = "0 0 0 1 * ?")
    public void ingestFestivalsMonthly() {
        System.out.println("Starting scheduled festival ingestion...");

        try {
            int count = ingestionService.ingestAllStateFestivals();
            System.out.println("Festival ingestion completed. Inserted: " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
