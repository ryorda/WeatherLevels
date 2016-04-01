package triaptahadi.ryorda.weathermonitoring.Model;

/**
 * @author Ryorda Triaptahadi
 *         A class containing weather information of a city
 */
public class CurrentData implements Comparable<CurrentData> {
    /**
     * @var cityName complete name of the City
     * @var weatherDesc A string which explain the weather condition
     * @var weatherCode A integer which denote a specific weather condition
     * @var temperature temperature in Celsius
     * @var humidity humidity in percentage
     * @var windSpeed wind speed in Kmph
     * @var OdaDate Custom date
     * @var imageUrl a link to get the appropriate image based on the weather status
     * @var shortName only the city name without country details
     */
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

    public String getWeatherDesc() {
        return weatherDesc;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public int compareTo(CurrentData another) {
        return cityName.compareTo(another.cityName);
    }
}
