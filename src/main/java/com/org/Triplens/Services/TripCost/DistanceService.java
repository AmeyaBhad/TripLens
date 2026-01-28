package com.org.Triplens.Services.TripCost;


//import com.org.Triplens.Services.TripCost.HaversineUtil;
import org.springframework.stereotype.Service;

@Service
public class DistanceService {

    public double getDistance(double[] src, double[] dest) {
        return HaversineUtil.calculateDistance(
            src[0], src[1],
            dest[0], dest[1]
        );
    }
}
