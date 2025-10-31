import java.util.List;
import java.util.Scanner;

import api.*;
import model.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите название места: ");
        String place = scanner.nextLine();

        LocationsFinder finder = new LocationsFinder();
        List<Location> locations = finder.searchLocations(place).join();

        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ") " + locations.get(i));
        }

        System.out.print("Введите номер понравившейся локации: ");
        int number = scanner.nextInt();
        Location selected = locations.get(number - 1);
        System.out.println();


        WeatherService ws = new WeatherService(selected.getLatitude(), selected.getLongitude());
        InterestingPlace ip = new InterestingPlace(selected.getLatitude(), selected.getLongitude());

        ws.getWeather().thenCombine(ip.getInterestingPlace(), (weather, interestingPlaces) -> {
            return new Result(weather, interestingPlaces);
        }).thenAccept(result -> {
            Weather weather = result.getWeather();
            List<InterestingLocations> places = result.getPlaces();

            System.out.printf("Температура: %.0f°C, ощущается как %.0f°C, Давление: %.2f, Влажность: %.2f\n",
                    weather.getTemperature(), weather.getFeelsLike(),
                    weather.getPressure(), weather.getHumidity());
            System.out.println();

            for (InterestingLocations pl : places) {
                DescriptionsOfLocations desc = new DescriptionsOfLocations(pl.getId());
                desc.getDescription().thenAccept(descriptions -> {
                    for (Description d : descriptions) {
                        System.out.println("Адрес: " + d.getAddressName() + ", Полное название: " +
                                d.getFullName() + ", Вид: " + d.getPurposeName());
                    }
                }).join();
            }
        }).join();
    }
}
