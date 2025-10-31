package api;

import model.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocationsFinder {
    private String API_KEY = "&key=9f78da32-a400-4f77-951d-2ffe48a72d65";
    private String URL = "https://graphhopper.com/api/1/geocode?q=";
    private HttpClient client = HttpClient.newHttpClient();
    private List<Location> locations;

    public LocationsFinder() {
        locations = new ArrayList<>();
    }

    public CompletableFuture<List<Location>> searchLocations(String query) {

        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String uri = URL + encoded + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parse);

    }

    private List<Location> parse(String body) {
        JSONObject json = new JSONObject(body);
        JSONArray hits = json.getJSONArray("hits");
        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            JSONObject point = hit.getJSONObject("point");
            double lat = point.getDouble("lat");
            double lng = point.getDouble("lng");
            String name = hit.getString("name");
            String country = hit.getString("country");
            String city = hit.getString("city");
            String state = hit.getString("state");
            Location location = new Location(lat, lng, name, country, city, state);
            locations.add(location);
        }
        return locations;
    }
}
