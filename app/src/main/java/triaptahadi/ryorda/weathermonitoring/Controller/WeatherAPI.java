package triaptahadi.ryorda.weathermonitoring.Controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import triaptahadi.ryorda.weathermonitoring.Model.CurrentData;
import triaptahadi.ryorda.weathermonitoring.Model.OdaJSONObject;

/**
 * Created by ryord on 3/30/2016.
 */
public class WeatherAPI {
    final String baseUrl = "https://api.worldweatheronline.com/premium/v1/weather.ashx?";
    final String keyApi = "93792bfdeac147eab2835650163003";
    final String cities[] = {
            "Jakarta", "London", "Singapore", "Kuala Lumpur", "Bandung"
    };
    private int currentIndex;

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
        Map<String, Object> map = new HashMap<>();
        map.put("format", "json");
        map.put("key", keyApi);
        map.put("q", cities[currentIndex]);
        map.put("num_of_days", 1);

        try {
            OdaJSONObject jsonObject = new OdaJSONObject(executeAPI(map));
            jsonObject = jsonObject.getJSONArray("current_condition").getJSONObject(0);

            double temper = jsonObject.getDouble("temp_C");
            double humid = jsonObject.getDouble("humidity");
            int code = jsonObject.getInt("weatherCode");
            String weatherDesc = jsonObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
            double windSpeed = jsonObject.getDouble("windspeedKmph");

            return new CurrentData(cities[currentIndex], weatherDesc, code, temper, humid, windSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String executeAPI(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        try {
            String params = "";
            boolean isFirst = true;

            if (map != null)
                for (String key : map.keySet()) {

                    if (!isFirst) params += "&";
                    isFirst = false;
                    params += key + "=" + map.get(key);
                }

            byte[] postData = params.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);

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
