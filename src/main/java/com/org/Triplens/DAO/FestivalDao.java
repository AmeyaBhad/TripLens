package com.org.Triplens.DAO;

import java.util.List;

import com.org.Triplens.entity.FestivalEntity;

public interface FestivalDao {

    void saveFestival(FestivalEntity festival);

    boolean festivalExists(String name, String state);

    List<FestivalEntity> findFestivalsByState(String state);
}
