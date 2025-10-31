package api;

import model.Description;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DescriptionsOfLocations {
    private String id;
    private String API_KEY = "&key=a9095021-fc78-48f0-9aaf-c9f2723c141c";
    HttpClient client = HttpClient.newHttpClient();

    public DescriptionsOfLocations(String id) {
        this.id = id;
    }

    public CompletableFuture<List<Description>> getDescription() {
        String URL = "https://catalog.api.2gis.com/3.0/items/byid?id=" +
                id + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parse);

    }
    private List<Description> parse(String body) {
        List<Description> descriptions = new ArrayList<>();
        JSONObject obj = new JSONObject(body);
        JSONObject result = obj.getJSONObject("result");
        JSONArray items = result.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String address_name = item.getString("address_name");
            String full_name = item.getString("full_name");
            String purpose_name = item.getString("purpose_name");
            descriptions.add(new Description(address_name, full_name, purpose_name));
        }
        return descriptions;
    }
}

