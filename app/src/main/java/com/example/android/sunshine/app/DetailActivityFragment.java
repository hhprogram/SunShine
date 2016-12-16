package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

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
        try {
            day = new JSONObject(dayDetail);
            System.out.println(dayDetail);
            JSONObject temp = day.getJSONObject("temp");
            long celsius = Math.round(temp.getDouble("day"));
            long farenheit = Math.round(celsius*9.0/5.0 + 32.00);
            long unixSeconds = day.getLong("dt");
            Date date = new Date(unixSeconds*1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("M-d-yy EE");
            String dayResult = sdf.format(date);
            String humid = String.valueOf(day.getDouble("humidity")) + "%";
            String pressure = String.valueOf(Math.round(day.getDouble("pressure"))) + "hPa";
            double windDegrees = Math.round(day.getDouble("deg"));
            JSONArray weather = day.getJSONArray("weather");
            System.out.println("2");
            String description = weather.getJSONObject(0).getString("description");
            setValues(celsius, farenheit, dayResult, humid, pressure, windDegrees, description,
                    container);
        } catch (JSONException e) {
            System.out.println("Cannot convert day detail to a JSON array");
        }

        return detail_RootView;
    }

    private void setValues(long celsius, long farenheit, String date, String humid, String pressure,
                            double degrees, String description, ViewGroup container) {
       TextView view = (TextView) container.findViewById(R.id.Celsius);
       view.setText(String.valueOf(celsius));
       view = (TextView) container.findViewById(R.id.Farenheit);
       view.setText(String.valueOf((farenheit)));
       view = (TextView) container.findViewById(R.id.Date);
       view.setText(date);
       view = (TextView) container.findViewById(R.id.Humidity);
       view.setText(humid);
       view = (TextView) container.findViewById(R.id.Pressure);
       view.setText(pressure);
       view = (TextView) container.findViewById(R.id.Wind);
       view.setText(String.valueOf((degrees)));
       view = (TextView) container.findViewById(R.id.Description);
       view.setText(description);
    }

    /**
     * Called to change the text value in a given textView
     * @param txt String that is to be put into the textView
     *            ID - int value for the textView ID whose text value we are changing
     */
    private void assignText(String txt, int id) {

    }
}
