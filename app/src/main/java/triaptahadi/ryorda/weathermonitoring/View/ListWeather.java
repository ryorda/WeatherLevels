package triaptahadi.ryorda.weathermonitoring.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import triaptahadi.ryorda.weathermonitoring.Controller.WeatherAPI;
import triaptahadi.ryorda.weathermonitoring.Model.CurrentData;
import triaptahadi.ryorda.weathermonitoring.Model.WeatherFragmentAdapter;
import triaptahadi.ryorda.weathermonitoring.R;

public class ListWeather extends AppCompatActivity {
    List<CurrentData> listWeather;
    boolean isRefreshing, isConfigured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_weather);

        isRefreshing = false;
        isConfigured = false;
        setup_tab();
        refreshList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (id) {
            case R.id.refresh_list:
                refreshList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshList() {
        if (isRefreshing) return;
        isRefreshing = true;

        findViewById(R.id.list_progressbar).setVisibility(View.VISIBLE);
        findViewById(R.id.black_cover).setVisibility(View.VISIBLE);
        findViewById(R.id.list_weather).setVisibility(View.GONE);

        final Intent i = new Intent(this, WeatherOverview.class);

        Thread listThread = new Thread() {
            public void run() {
                try {
                    WeatherAPI weatherAPI = new WeatherAPI("Jakarta");
                    listWeather = weatherAPI.getAllCitiesData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final ListView listView = ((ListView) findViewById(R.id.list_weather));
                final WeatherFragmentAdapter adapter = new WeatherFragmentAdapter(getApplicationContext(), listWeather);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        CurrentData currentData = listWeather.get(position);
                        SharedPreferences mPref = getSharedPreferences("PREF_WEATHER", MODE_PRIVATE);

                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putString("cityName", currentData.getCityName());
                        editor.putString("weatherDesc", currentData.getWeatherDesc());
                        editor.putInt("weatherCode", currentData.getWeatherCode());
                        editor.putFloat("temperature", (float) currentData.getTemperature());
                        editor.putFloat("humidity", (float) currentData.getHumidity());
                        editor.putFloat("windSpeed", (float) currentData.getWindSpeed());
                        editor.putString("imageUrl", currentData.getImageUrl());
                        editor.commit();

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);

                        findViewById(R.id.list_progressbar).setVisibility(View.GONE);
                        findViewById(R.id.black_cover).setVisibility(View.GONE);
                        findViewById(R.id.list_weather).setVisibility(View.VISIBLE);
                        onWindowFocusChanged(true);
                    }
                });

                isRefreshing = false;

            }
        };
        listThread.start();

    }

    private void setup_tab() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setNavigationIcon(null);
        toolbar.setTitle("WEATHER MONITORING");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isConfigured) {
            try {
                Point screen = new Point();
                getWindowManager().getDefaultDisplay().getSize(screen);

                ListView listView = (ListView) findViewById(R.id.list_weather);
                listView.getLayoutParams().width = screen.x;

                View item = listView.getAdapter().getView(0, null, listView);
                item.measure(0, 0);

                listView.getLayoutParams().height = 33 * (item.getMeasuredHeight() + listView.getDividerHeight());
                isConfigured = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
