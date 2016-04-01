package triaptahadi.ryorda.weathermonitoring.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Ryorda Triaptahadi
 *         A class for parsing a JSON Object
 */
public class OdaJSONObject {
    /**
     * @var values key-value pair of this JSON Object
     */
    Map<String, String> values;

    /**
     * Constructor
     *
     * @param json String in JSON format
     * @throws Exception If there are some fatal mistakes: not a JSON Object, invalid JSON format
     */
    public OdaJSONObject(String json) throws Exception {
        values = new HashMap<>();
        json = json.trim();

        if (json.charAt(0) != '{')
            throw new Exception("data JSON : " + json + " is not a JSON Object.");

        Stack<Character> balancing = new Stack<>();
        json = json.substring(1, json.length() - 1);

        boolean insideString = false;
        int pos = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            switch (c) {
                case '"':
                    insideString = !insideString;
                    break;
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
                    if (balancing.isEmpty() && !insideString) {
                        saveData(json.substring(pos, i));
                        pos = i + 1;
                    }
            }
        }

        saveData(json.substring(pos));
    }

    /**
     * A method for parsing the given become key and value which will be saved in the map
     *
     * @param json
     */
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