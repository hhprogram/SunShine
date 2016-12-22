package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view. This is the java file that hosts the layout
 * for the detailed view of the forecast
 */
public class DetailActivityFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    final static int NUM_DETAILS = 7;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        JSONObject day = null;
        View detail_RootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent source = getActivity().getIntent();
        String dayDetail = source.getStringExtra(ForecastFragment.LIST_DATA);
        //gets the double value associated with the string key INDEX, 2nd parameter is the default
        //value if that key doesn't exist
        Integer index = source.getIntExtra(ForecastFragment.INDEX, -1);
        try {
            day = new JSONObject(dayDetail);
//            System.out.println(dayDetail);
            JSONObject temp = day.getJSONObject("temp");
            long celsius = Math.round(temp.getDouble("day"));
            long farenheit = Math.round(celsius*9.0/5.0 + 32.00);
            String dayResult = makeDate(index ,day.getLong("dt"));
            String humid = String.valueOf(day.getDouble("humidity")) + "%";
            String pressure = String.valueOf(Math.round(day.getDouble("pressure"))) + "hPa";
            double windDegrees = day.getDouble("deg");
            String windDirection = getWindDirection(windDegrees);
            double windSpeed = day.getDouble("speed");
            long imperialSpeed = convertWindtoImperial(windSpeed);
            JSONArray weather = day.getJSONArray("weather");
//            System.out.println("2");
            JSONObject weatherDetails = weather.getJSONObject(0);
            String description = weatherDetails.getString("description");
            Drawable icon = getImage(description);
            setValues(celsius, farenheit, dayResult, humid, pressure, windDirection, imperialSpeed
                    , description, icon, container);
        } catch (JSONException e) {
            System.out.println("Cannot convert day detail to a JSON array");
        }

        return detail_RootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("ALsjdkajsdklj - fragment");
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Assigns a proper string to the DATE variable used to assign value to one of the textviews
     * @param index the index within the JSON array that holds the requested weater forecast. Used
     *              to determine if it is today or tomorrow or other
     * @param seconds unix seconds to determine the exact data
     * @return a string that denotes the date of detailed forecast
     */
    private String makeDate(int index, long seconds) {
        StringBuilder output = new StringBuilder();
        Date date = new Date(seconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EE");
        String num = sdf.format(date);
        if (index == 0){
            output.append("Today - ");
            output.append(num);
        } else if (index == 1) {
            output.append("Tomorrow - ");
            output.append(num);
        } else {
            String weekday = sdf2.format(date);
            output.append(weekday + " - ");
            output.append(num);
        }
        return output.toString();
    }

    /**
     * Returns the compass String  that corresponds to the degrees
     * @param degrees the exact degree forecast of the wind direction
     * @return a string {}N, NW, W, SW, S, SE, E, NE}
     */
    private String getWindDirection(double degrees) {
        //need 9 because need to have North in there twice as it corresponds to 0 and 360
        HashMap<Double, String> compass = new HashMap<>(9);
        Double[] direction = {0.0, 360.0,45.0,90.0,135.0,180.0,225.0,270.0,315.0};
        double midpt = 22.5;
        Double[] diff;
        compass.put(0.0, "N");
        compass.put(360.0, "N");
        compass.put(45.0, "NE");
        compass.put(90.0, "E");
        compass.put(135.0, "SE");
        compass.put(180.0, "S");
        compass.put(225.0, "SW");
        compass.put(270.0, "W");
        compass.put(315.0, "NW");
        for (double i: direction) {
            if (Math.abs(i - degrees) <= midpt) {
                return compass.get(i);
            }
        }
        return "Something wrong with wind converter";
    }

    /**
     * Converts meter/s wind speed into MPH
     * @param metricSpeed
     * @return a rounded long of the MPH value
     */
    private long convertWindtoImperial(double metricSpeed) {
        double milesPerMeter = 1.0/(1609.34);
        double secsPerHr = 3600.00;
        double output = metricSpeed*milesPerMeter;
        output *= secsPerHr;
        return Math.round(output);
    }

    /**
     * Retrieves the relevant image for the weather description given
     * @param description String summary of average weather ex.) slightly cloudy
     * @return Drawable in the image res/drawable folder
     * weather description and what they map to seen here
     * (docs : http://openweathermap.org/weather-conditions)
     * Some of them are changed but the general mapping is there (couldn't get the URL to work) so
     * using images actually in the res folder vs. through openweathermaps URL
     */
    private Drawable getImage(String description) {
        //need a better way to map the descriptions to PNG files
        HashMap<String, String> icons = new HashMap<>();
        icons.put("clear sky", "art_clear");
        icons.put("few clouds", "art_light_clouds");
        icons.put("scattered clouds", "art_light_clouds");
        icons.put("broken clouds", "art_light_clouds");
        icons.put("light rain", "art_light_rain");
        icons.put("rain", "art_rain");
        icons.put("thunderstorm", "art_storm");
        icons.put("snow", "art_snow");
        icons.put("light snow", "art_snow");
        icons.put("mist", "art_fog");
        String imgDesc = icons.get(description);
        //gets a RESOURCES object to easily access XML resources (like drawables, or strings or
        //anything in the 'res' subfolder
        Resources resource = getResources();
        //use the method get Drawable object associated with the resource ID (int)
        //getIdentifier gets a resource ID in INT form that matches the string IMGDESC (the .png
        //ie the extension of file can be left out)
        //NAME - can be just the file name or the whole file path. If just the file name then the
        //second paramter "defType" needs to denote the subfolder under resources where the specific
        //file is found in. The 3rd parameter is the package used in the app (need to understand
        //what referencing the package does - the package is found in the "AndroidManiFest.xml file
        //and the "package" label in it
        Drawable actualImg = resource.getDrawable(resource.getIdentifier("drawable/" + imgDesc,null,
                getActivity().getPackageName()));
//        Drawable actualImg = resource.getDrawable(resource.getIdentifier(imgDesc, "drawable",
//                "com.example.android.sunshine.app"));
        return actualImg;
    }

    /**
     * Sets all the text views, in the detailed view, to values to show a more detailed weather
     * forecast for that day
     * @param celsius temperature
     * @param farenheit temperature
     * @param date the data of the specific detailed view. Will Say either: Today - Month/Day,
     *             Tomorrow - Month/Day, Day of the Week - Month/Day
     * @param humid % humidity
     * @param pressure air pressure
     * @param degrees Wind direction in degrees...where 0 and 360 are north
     * @param description ex. cloudy, clear etc...
     * @param container of the View
     */
    private void setValues(long celsius, long farenheit, String date, String humid, String pressure,
                            String degrees, long speed, String description, Drawable icon,
                           ViewGroup container) {
       TextView view = (TextView) container.findViewById(R.id.Celsius);
       view.setText(String.valueOf(celsius) + " C");
       view = (TextView) container.findViewById(R.id.Farenheit);
       view.setText(String.valueOf((farenheit)) + " F");
       view = (TextView) container.findViewById(R.id.Date);
       view.setText(date);
       view = (TextView) container.findViewById(R.id.Humidity);
       view.setText("humidity: "+ humid);
       view = (TextView) container.findViewById(R.id.Pressure);
       view.setText("pressire: " + pressure);
       view = (TextView) container.findViewById(R.id.Wind);
       view.setText("Wind:" + String.valueOf(speed) + " MPH - " + degrees);
       view = (TextView) container.findViewById(R.id.Description);
       view.setText(description);
       ImageView imgView = (ImageView) container.findViewById(R.id.Icon);
       imgView.setImageDrawable(icon);

    }

}
