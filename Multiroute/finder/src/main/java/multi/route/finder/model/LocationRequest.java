package multi.route.finder.model;



import java.util.List;

public class LocationRequest {

    // Each location = [longitude, latitude]
    private List<List<Double>> coordinates;

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}

