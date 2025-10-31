package api;

import model.Weather;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherService {
    private double latitude;
    private double longitude;
    private String URL = "https://api.openweathermap.org/data/2.5/weather?lat=";
    private String API_KEY = "540df3fb55d59a7dc03672e9d850e5fe";
    private HttpClient client = HttpClient.newHttpClient();

    public WeatherService(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CompletableFuture<Weather> getWeather() {

        String url = URL + latitude + "&lon=" + longitude + "&appid=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parse);

    }
    private Weather parse(String body) {
        JSONObject obj = new JSONObject(body);
        JSONObject main = obj.getJSONObject("main");
        double temperature = main.getDouble("temp");
        double temp = temperature - 273.15;
        double feelsLike = main.getDouble("feels_like");
        double feelsLikeTemp = feelsLike - 273.15;
        double pressure = main.getDouble("pressure");
        double humidity = main.getDouble("humidity");
        return new Weather(temp, feelsLikeTemp, pressure, humidity);
    }
}

