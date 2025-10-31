package api;

import org.json.JSONArray;
import org.json.JSONObject;

import model.InterestingLocations;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InterestingPlace {
    private double latitude;
    private double longitude;
    private HttpClient client = HttpClient.newHttpClient();
    private String API_KEY = "&key=a9095021-fc78-48f0-9aaf-c9f2723c141c";
    private List<InterestingLocations> interestingPlaces;

    public InterestingPlace(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CompletableFuture<List<InterestingLocations>> getInterestingPlace() {
        String url = "https://catalog.api.2gis.com/3.0/items" +
                "?q=достопримечательности" +
                "&location=" + longitude + "," + latitude +
                "&radius=5000" +
                "&page_size=5" +
                API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parse);

    }
    private List<InterestingLocations> parse(String body) {
        interestingPlaces = new ArrayList<InterestingLocations>();
        JSONObject obj = new JSONObject(body);
        JSONObject results = obj.getJSONObject("result");
        JSONArray items = results.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String id = item.getString("id");
            String name = item.getString("name");
            String type = item.getString("type");
            interestingPlaces.add(new InterestingLocations(id, name, type));
        }
        return interestingPlaces;

    }
}

