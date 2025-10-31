package model;

public class Location {
    private double latitude;
    private double longitude;
    private String name;
    private String country;
    private String city;
    private String state;

    public Location(double latitude, double longitude, String name, String country, String city, String state) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.country = country;
        this.city = city;
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Широта: " + latitude + ", Долгота: " + longitude +
                ", Имя: " + name + ", Страна: " + country +
                ", Город: " + city + ", Область: " + state;
    }
}
