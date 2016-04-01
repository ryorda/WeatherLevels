package triaptahadi.ryorda.weathermonitoring.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import triaptahadi.ryorda.weathermonitoring.Model.CurrentData;
import triaptahadi.ryorda.weathermonitoring.Model.OdaJSONObject;

/**
 * @author Ryorda Triaptahadi
 *         A class which is used to send request with World Weather Online API
 */
public class WeatherAPI {
    /**
     * @var baseUrl the basic url of worldweatheronline API
     * @var keyApi my key Api
     * @var cities array of available cities
     * @var currentIndex an index showing current city position in variable cities
     */
    private final String baseUrl = "https://api.worldweatheronline.com/premium/v1/weather.ashx";
    private final String keyApi = "93792bfdeac147eab2835650163003";
    private final String cities[] = {
            "Jakarta", "London", "Singapore", "Kuala Lumpur", "Bandung",
            "Johor", "Depok", "Jogjakarta", "Pontianak", "Palu",
            "Manhattan", "Bangkok", "Taipei", "Manila", "Beijing",
            "Washington", "New York", "Manchester", "Liverpool", "Dublin",
            "Barcelona", "Sydney", "Hong Kong", "Padang", "Berlin",
            "Johannesburg", "Hamburg", "Mecca", "Istanbul", "Rome",
            "Melbourne", "Texas", "Seoul"
    };
    private int currentIndex;
    private CurrentData currentData;

    /**
     * Constructor
     *
     * @param s The name of city which becomes default city
     * @throws Exception If the given city name is not supported or unavailable
     */
    public WeatherAPI(String s) throws Exception {
        currentIndex = -1;
        for (int i = 0; i < cities.length; i++)
            if (cities[i].equalsIgnoreCase(s)) {
                currentIndex = i;
                break;
            }

        if (currentIndex == -1)
            throw new Exception("Sorry your city is not available.");
    }

    /**
     * A method to get current weather information from this current city
     *
     * @return CurrentData weather information of the current city
     */
    public CurrentData getCurrentData() {

        final String city_name = cities[currentIndex].replaceAll("\\s", "%20");
        final Map<String, Object> map = new HashMap<>();
        map.put("format", "json");
        map.put("q", city_name);
        map.put("num_of_days", 1);
        map.put("fx", "no");
        map.put("mca", "no");
        map.put("fx24", "no");
        map.put("includelocation", "no");


        Thread internetThread = new Thread() {
            @Override
            public void run() {
                try {
                    OdaJSONObject jsonObject = new OdaJSONObject(executeAPI(map));

                    String cityName = jsonObject.getJSONObject("data").getJSONArray("request").getJSONObject(0).getString("query");

                    jsonObject = jsonObject.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0);

                    double temper = jsonObject.getDouble("temp_C");
                    double humid = jsonObject.getDouble("humidity");
                    int code = jsonObject.getInt("weatherCode");
                    String weatherDesc = jsonObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                    double windSpeed = jsonObject.getDouble("windspeedKmph");
                    String imageUrl = jsonObject.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");

                    currentData = new CurrentData(cityName, weatherDesc, code, temper, humid, windSpeed, imageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    currentData = null;
                }
            }
        };

        internetThread.start();
        try {
            internetThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            internetThread.interrupt();
        }
        return currentData;
    }

    /**
     * A method to get weather information from all available cities
     *
     * @return List<CurrentData> list of current data of all cities
     */
    public List<CurrentData> getAllCitiesData() {
        final ArrayList<CurrentData> data = new ArrayList<>();
        Thread internetThread[] = new Thread[cities.length];

        /*
         * loop to get weather information of a city one by one
         */
        for (int i = 0; i < cities.length; i++) {
            final String city_name = cities[i].replaceAll("\\s", "%20");

            final Map<String, Object> map = new HashMap<>();
            map.put("format", "json");
            map.put("q", city_name);
            map.put("num_of_days", 1);
            map.put("fx", "no");
            map.put("mca", "no");
            map.put("fx24", "no");
            map.put("includelocation", "no");


            /*
             * Thread for calling API
             */
            internetThread[i] = new Thread() {
                @Override
                public void run() {
                    CurrentData temp;
                    try {
                        OdaJSONObject jsonObject = new OdaJSONObject(executeAPI(map));
                        String cityName = jsonObject.getJSONObject("data").getJSONArray("request").getJSONObject(0).getString("query");

                        jsonObject = jsonObject.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0);

                        double temper = jsonObject.getDouble("temp_C");
                        double humid = jsonObject.getDouble("humidity");
                        int code = jsonObject.getInt("weatherCode");
                        String weatherDesc = jsonObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                        double windSpeed = jsonObject.getDouble("windspeedKmph");
                        String imageUrl = jsonObject.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");

                        temp = new CurrentData(cityName, weatherDesc, code, temper, humid, windSpeed, imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        temp = null;
                    }

                    data.add(temp);
                }
            };

            internetThread[i].start();

        }

        /*
         * loop to ensure each thread has stopped and the data have been added to list
         */
        for (int i = 0; i < cities.length; i++)
            try {
                internetThread[i].join();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return data;
    }

    /**
     * A special method for calling the API directly
     *
     * @param map a map consisting attributes to be used in HTTP GET Method when calling the API
     * @return String in JSON format
     */
    private String executeAPI(Map<String, Object> map) {
        String link = baseUrl + "?key=" + keyApi;
        StringBuilder sb = new StringBuilder();
        try {

            if (map != null)
                for (String key : map.keySet()) {

                    link += "&" + key + "=" + map.get(key);
                }

//            byte[] postData = {0};
//            int postDataLength = postData.length;
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
//            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
//            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
//            wr.write(postData);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            for (int ch = br.read(); ch != -1; ch = br.read())
                sb.append((char) ch);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
