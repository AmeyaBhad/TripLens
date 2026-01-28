package com.org.Triplens.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Triplens.DAO.FestivalDao;
import com.org.Triplens.Services.StateExtractor.CityStateResolver;
import com.org.Triplens.entity.FestivalEntity;

@Service
public class FestivalQueryService {

    @Autowired
    private FestivalDao festivalDao;

    @Autowired
    private CityStateResolver cityStateResolver;

    public List<FestivalEntity> getFestivalsByCity(String city) {

        String state = cityStateResolver.resolveState(city);
        return festivalDao.findFestivalsByState(state);
    }
}

