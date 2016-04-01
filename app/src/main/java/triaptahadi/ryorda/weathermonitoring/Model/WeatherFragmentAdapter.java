package triaptahadi.ryorda.weathermonitoring.Model;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import triaptahadi.ryorda.weathermonitoring.R;

/**
 * @author Ryorda Triaptahadi
 * A class which be the listview adapter of ListWeather.class
 */
public class WeatherFragmentAdapter extends ArrayAdapter<CurrentData> {
    /**
     * @var c current context
     * @var currentDatas list of CurrentData
     * @var icon a bitmap variable which will be used in the listView
     * @var pref the sharedpreferences
     */
    Context c;
    List<CurrentData> currentDatas;
    Bitmap icon;
    SharedPreferences pref;

    /**
     * @param c    current context
     * @param list list of CurrentData
     */
    public WeatherFragmentAdapter(Context c, List<CurrentData> list) {
        super(c, android.R.layout.simple_list_item_1, list);
        this.c = c;
        currentDatas = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CurrentData item = currentDatas.get(position);
        pref = c.getSharedPreferences("IMAGES_PATH", c.MODE_PRIVATE);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (View) inflater.inflate(
                    R.layout.weather_list_layout, null);
        }

        icon = null;
        final View v = convertView;
        String path = pref.getString(item.getWeatherCode() + "", null);
        icon = loadImage(path, item.getWeatherCode() + "");

        Thread imageThread = new Thread() {
            @Override
            public void run() {
                try {
                    InputStream in = new java.net.URL(item.getImageUrl()).openStream();
                    icon = BitmapFactory.decodeStream(in);

                    String pathToFile = saveImage(icon, item.getWeatherCode() + "");
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(item.getWeatherCode() + "", pathToFile);
                    edit.commit();

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        if (icon == null) imageThread.start();

        TextView name = (TextView) convertView.findViewById(R.id.list_city_name);
        TextView temperature = (TextView) convertView.findViewById(R.id.list_temperature);

        name.setText(item.getCityName());
        temperature.setText((int) item.getTemperature() + " \u2103");

        try {
            imageThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        icon = Bitmap.createScaledBitmap(icon, 3 * icon.getHeight(), 3 * icon.getWidth(), true);
        ImageView image = (ImageView) v.findViewById(R.id.list_weather_icon);
        image.setImageBitmap(icon);

        return convertView;
    }

    /**
     * A method for saving an image to an app folder
     * @param bitmapImage the image file
     * @param name name of file
     * @return String of the app folder path containing the image
     */
    private String saveImage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(c);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File mypath = new File(directory, name + ".jpg");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }

    /**
     * A method to load image from app folders
     * @param path the app folder path
     * @param name name of file
     * @return Bitmap image decoded from the file, null if not exists
     */
    private Bitmap loadImage(String path, String name) {
        try {
            File f = new File(path, name + ".jpg");
            Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
