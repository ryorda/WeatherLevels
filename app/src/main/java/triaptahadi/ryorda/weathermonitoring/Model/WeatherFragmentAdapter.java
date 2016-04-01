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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import triaptahadi.ryorda.weathermonitoring.R;

/**
 * Created by ryord on 3/31/2016.
 */
public class WeatherFragmentAdapter extends ArrayAdapter<CurrentData> {
    Context c;
    List<CurrentData> currentDatas;
    Bitmap icon;
    SharedPreferences pref;

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
        String path = pref.getString(item.getWeatherDesc(), null);
        icon = loadImage(path, item.getWeatherDesc());

        Thread imageThread = new Thread() {
            @Override
            public void run() {
                try {
                    InputStream in = new java.net.URL(item.getImageUrl()).openStream();
                    icon = BitmapFactory.decodeStream(in);

                    pref.edit().putString(item.getWeatherDesc(), saveImage(icon, item.getWeatherDesc()));
                    pref.edit().commit();

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

    private String saveImage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(c);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File mypath = new File(directory, name + ".jpg");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }

    private Bitmap loadImage(String path, String name) {
        try {
            File f = new File(path, name + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }


}
