package triaptahadi.ryorda.weathermonitoring.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A class for parsing JSON as JSON Object
 */
public class OdaJSONObject {
    Map<String, String> values;

    public OdaJSONObject(String json) throws Exception {
        values = new HashMap<>();
        json = json.trim();

        if (json.charAt(0) != '{')
            throw new Exception("data JSON : " + json + " is not a JSON Object.");

        Stack<Character> balancing = new Stack<>();
        json = json.substring(1, json.length() - 1);

        int pos = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            switch (c) {
                case '[':
                case '{':
                    balancing.push(c);
                    break;
                case '}':
                    if (balancing.isEmpty() || balancing.pop() != '{')
                        throw new Exception("Invalid json syntax : " + json);
                    break;
                case ']':
                    if (balancing.isEmpty() || balancing.pop() != '[')
                        throw new Exception("Invalid json syntax : " + json);
                    break;
                case ',':
                    if (balancing.isEmpty()) {
                        saveData(json.substring(pos, i));
                        pos = i + 1;
                    }
            }
        }

        saveData(json.substring(pos));
    }

    private void saveData(String json) {
        int idx = json.indexOf(':');
        String key = json.substring(0, idx).trim();
        String val = json.substring(idx + 1).trim();

        if (key.charAt(0) == '"')
            key = key.substring(1, key.length() - 1);
        if (val.charAt(0) == '"')
            val = val.substring(1, val.length() - 1);

        values.put(key, val);
    }

    public int getInt(String s) throws Exception {
        String val = values.get(s);

        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONObject : " + val + " : can't be parsed to be an integer");
        }
    }

    public double getDouble(String s) throws Exception {
        String val = values.get(s);
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONObject : " + val + " : can't be parsed to be floating number");
        }
    }

    public String getString(String s) throws Exception {
        return values.get(s);
    }

    public OdaJSONObject getJSONObject(String s) throws Exception {
        return new OdaJSONObject(values.get(s));
    }

    public OdaJSONArray getJSONArray(String s) throws Exception {
        return new OdaJSONArray(values.get(s));
    }
}