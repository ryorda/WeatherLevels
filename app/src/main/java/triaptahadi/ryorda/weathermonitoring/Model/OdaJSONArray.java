package triaptahadi.ryorda.weathermonitoring.Model;

import java.util.Stack;

/**
 * @author Ryorda Triaptahadi
 *         A class for parsing a JSON Array
 */
public class OdaJSONArray {
    final int MAX_VAL = 1_000_000;
    String[] values;

    /**
     * Constructor
     *
     * @param json String in JSON format
     * @throws Exception If there are some fatal mistakes: not a JSON Array, invalid JSON format
     */
    public OdaJSONArray(String json) throws Exception {

        values = new String[MAX_VAL];

        json = json.trim();
        if (json.charAt(0) != '[')
            throw new Exception("It is not a Json Array:\n" + json);

        Stack<Character> balancing = new Stack<>();
        json = json.substring(1, json.length() - 1);

        int pos = 0;
        int idx = 0;
        boolean insideString = false;

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
                        values[idx] = json.substring(pos, i).trim();
                        if (values[idx].charAt(0) == '"')
                            values[idx] = values[idx].substring(1, values[idx].length() - 1);
                        idx++;
                        pos = i + 1;
                    }
            }
        }

        values[idx++] = json.substring(pos);
    }

    public int getInt(int index) throws Exception {
        try {
            return Integer.parseInt(values[index]);
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONArray : " + values[index] + " : can't be parsed to be an integer");
        }
    }

    public double getDouble(int index) throws Exception {
        try {
            return Double.parseDouble(values[index]);
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONArray : " + values[index] + " : can't be parsed to be floating number");
        }
    }

    public String getString(int index) throws Exception {
        return values[index];
    }

    public OdaJSONObject getJSONObject(int index) throws Exception {
        return new OdaJSONObject(values[index]);
    }

    public OdaJSONArray getJSONArray(int index) throws Exception {
        return new OdaJSONArray(values[index]);
    }
}
