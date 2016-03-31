package triaptahadi.ryorda.weathermonitoring.Model;

/**
 * Created by ryord on 3/30/2016.
 */
public class CurrentData {
    private String cityName, weatherDesc;
    private int weatherCode;
    private double temperature, humidity, windSpeed;
    private OdaDate date;
    private String imageUrl, shortName;

    public CurrentData(String cityName, String weatherName, int weatherCode, double temperature, double humidity, double windSpeed, String imageUrl, OdaDate date) {
        this.cityName = cityName;
        this.weatherDesc = weatherName;
        this.weatherCode = weatherCode;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.imageUrl = imageUrl;
        this.date = date;

        shortName = cityName.split(",")[0].trim();
    }

    public CurrentData(String cityName, String weatherDesc, int weatherCode, double temperature, double humidity, double windSpeed, String imageUrl) {
        this(cityName, weatherDesc, weatherCode, temperature, humidity, windSpeed, imageUrl, null);
    }

    public String getCityName() {
        return cityName;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public OdaDate getDate() {
        return date;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getShortName() {
        return shortName;
    }
}
