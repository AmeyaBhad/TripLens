package com.org.Triplens.DAO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.entity.FestivalEntity;
import com.org.Triplens.repository.FestivalRepository;

@Component
public class FestivalDaoImpl implements FestivalDao {

    @Autowired
    private FestivalRepository repository;

    @Override
    public void saveFestival(FestivalEntity festival) {
        repository.save(festival);
    }

    @Override
    public boolean festivalExists(String name, String state) {
        return repository.existsByNameIgnoreCaseAndStateIgnoreCase(name, state);
    }

    @Override
    public List<FestivalEntity> findFestivalsByState(String state) {
        return repository.findByStateIgnoreCase(state);
    }
}

