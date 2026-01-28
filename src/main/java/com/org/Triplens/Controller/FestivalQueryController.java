package com.org.Triplens.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.DAO.FestivalDao;
import com.org.Triplens.Services.StateExtractor.CityStateResolver;
import com.org.Triplens.entity.FestivalEntity;

@RestController
@RequestMapping("/api/festivals")
@CrossOrigin(origins = "http://localhost:5173")
public class FestivalQueryController {

    @Autowired private FestivalDao festivalDao;
    @Autowired private CityStateResolver cityStateResolver;

    @GetMapping
    public List<FestivalEntity> getFestivals(@RequestParam String city) {
        String state = cityStateResolver.resolveState(city);
        return festivalDao.findFestivalsByState(state);
    }
}
