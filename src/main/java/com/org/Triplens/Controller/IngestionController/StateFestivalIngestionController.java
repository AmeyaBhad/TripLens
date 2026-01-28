package com.org.Triplens.Controller.IngestionController;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.Services.Festivals.StateFestivalIngestionService;

@RestController
@RequestMapping("/api/admin/festivals")
public class StateFestivalIngestionController {

    @Autowired
    private StateFestivalIngestionService ingestionService;

    @PostMapping("/ingest-all")
    public Map<String, Object> ingest() throws Exception {

        int inserted = ingestionService.ingestAllStateFestivals();

        return Map.of("status","SUCCESS","inserted", inserted);
    }
}
