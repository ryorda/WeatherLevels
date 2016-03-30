package triaptahadi.ryorda.weathermonitoring.Model;

/**
 * Created by ryord on 3/30/2016.
 */
public class OdaJSONArray {
    String[] values;

    public OdaJSONArray(String json) throws Exception {
        json = json.trim();
        if (json.charAt(0) != '{')
            throw new Exception("It is not a Json Array:\n" + json);

        json = json.substring(1, json.length());
        values = json.split(",");
    }

    public int getInt(int index) throws Exception {
        try {
            return Integer.parseInt(values[index]);
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONArray : " + index + " : can't be parsed to be an integer");
        }
    }

    public double getDouble(int index) throws Exception {
        try {
            return Double.parseDouble(values[index]);
        } catch (NumberFormatException e) {
            throw new Exception("OdaJSONArray : " + index + " : can't be parsed to be floating number");
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
