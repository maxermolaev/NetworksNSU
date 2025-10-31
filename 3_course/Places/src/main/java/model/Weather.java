package model;

public class Weather {
    private double temperature;
    private double humidity;
    private double pressure;
    private double feelsLike;

    public Weather(double temperature, double feelsLike, double pressure, double humidity) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.humidity = humidity;


    }

    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public double getPressure() {
        return pressure;
    }
    public double getHumidity() {
        return humidity;
    }
}
