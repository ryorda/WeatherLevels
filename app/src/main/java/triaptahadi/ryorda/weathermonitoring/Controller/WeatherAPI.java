package triaptahadi.ryorda.weathermonitoring.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import triaptahadi.ryorda.weathermonitoring.Model.CurrentData;
import triaptahadi.ryorda.weathermonitoring.Model.OdaJSONObject;

/**
 * Created by ryord on 3/30/2016.
 */
public class WeatherAPI {
    private final String baseUrl = "https://api.worldweatheronline.com/premium/v1/weather.ashx";
    private final String keyApi = "93792bfdeac147eab2835650163003";
    private final String cities[] = {
            "Jakarta", "London", "Singapore", "Kuala Lumpur", "Bandung"
    };
    private int currentIndex;
    private CurrentData currentData;

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

    public CurrentData getCurrentData() {
        final Map<String, Object> map = new HashMap<>();
        map.put("format", "json");
        map.put("q", cities[currentIndex]);
        map.put("num_of_days", 1);

        Thread internetThread = new Thread() {
            @Override
            public void run() {
                try {
                    OdaJSONObject jsonObject = new OdaJSONObject(executeAPI(map));
                    jsonObject = jsonObject.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0);

                    double temper = jsonObject.getDouble("temp_C");
                    double humid = jsonObject.getDouble("humidity");
                    int code = jsonObject.getInt("weatherCode");
                    String weatherDesc = jsonObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                    double windSpeed = jsonObject.getDouble("windspeedKmph");

                    currentData = new CurrentData(cities[currentIndex], weatherDesc, code, temper, humid, windSpeed);
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
