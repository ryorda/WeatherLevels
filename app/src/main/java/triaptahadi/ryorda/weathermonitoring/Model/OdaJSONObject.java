package triaptahadi.ryorda.weathermonitoring.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A class for parsing JSON as JSON Object
 */
public class OdaJSONObject {
    Map<String, String> values;

    public OdaJSONObject(String json) throws Exception {
        json = json.trim();
        if (json.charAt(0) != '{')
            throw new Exception("It is not a Json Object:\n" + json);
        values = new HashMap<>();

        json = json.substring(1, json.length());

        StringTokenizer dataJson = new StringTokenizer(json, ",");
        while (dataJson.hasMoreTokens()) {
            String[] data = dataJson.nextToken().split(":");
            values.put(data[0], data[1]);
        }
    }

    public int getInt(String s) throws Exception {
        s = "\"" + s + "\"";
        try {
            return Integer.parseInt(values.get(s));
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONObject : " + s + " : can't be parsed to be an integer");
        }
    }

    public double getDouble(String s) throws Exception {
        s = "\"" + s + "\"";
        try {
            return Double.parseDouble(values.get(s));
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONObject : " + s + " : can't be parsed to be floating number");
        }
    }

    public String getString(String s) throws Exception {
        s = "\"" + s + "\"";
        return values.get(s);
    }

    public OdaJSONObject getJSONObject(String s) throws Exception {
        s = "\"" + s + "\"";
        return new OdaJSONObject(values.get(s));
    }

    public OdaJSONArray getJSONArray(String s) throws Exception {
        s = "\"" + s + "\"";
        return new OdaJSONArray(values.get(s));
    }
}
