package model;

import java.util.List;

public class Result {
    public Weather weather;
    public List<InterestingLocations> places;

    public Result(Weather weather, List<InterestingLocations> places) {
        this.weather = weather;
        this.places = places;
    }

    public Weather getWeather() {
        return weather;
    }
    public List<InterestingLocations> getPlaces() {
        return places;
    }
}

