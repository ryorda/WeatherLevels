package triaptahadi.ryorda.weathermonitoring.Model;

import java.util.StringTokenizer;

/**
 * Created by ryord on 3/30/2016.
 */
public class OdaDate {
    private int date, month, year;

    public OdaDate(String s) throws Exception {
        StringTokenizer details = new StringTokenizer(s);

        try {
            year = Integer.parseInt(details.nextToken());
            month = Integer.parseInt(details.nextToken());
            date = Integer.parseInt(details.nextToken());
        } catch (NumberFormatException e) {
            throw new Exception("Date " + s + " is an invalid format. ");
        }

    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
