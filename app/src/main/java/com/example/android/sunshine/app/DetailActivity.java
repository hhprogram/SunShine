package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DetailActivity extends ActionBarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailActivityFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Once I click the "settings" menu button then I create an intent that originates
            // from THIS activity (note since this is an activity class just say THIS, in a
            //fragment need to call getActivity. Then I want to activate SettingsActivity explicitly
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        System.out.println("new pref: "+ sharedPref.getString(getString(R.string.zip_entry), ""));
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
            }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("Helloooo");
        if (key.equals(getString(R.string.zip_entry))) {
            try {
                GetRequest newRequest = new GetRequest();
                String newData = newRequest.execute(sharedPreferences.getString(key, "")).get();
                Intent dayData = this.getIntent();
                int dayNum = dayData.getIntExtra(ForecastFragment.INDEX,-1);
                JSONObject forecast = new JSONObject(newData);
                JSONArray week = forecast.getJSONArray("list");
                JSONObject newDay = week.getJSONObject(dayNum);
                Intent update = new Intent(this, DetailActivity.class);
                Bundle newBundle = new Bundle();
                newBundle.putInt(ForecastFragment.INDEX, dayNum);
                newBundle.putString(ForecastFragment.LIST_DATA, newDay.toString());
                update.putExtras(newBundle);
                startActivity(update);
                finish();
            } catch (ExecutionException e) {
                System.out.println("Something wrong with detailed zip refresh");
            } catch (InterruptedException e) {
                System.out.println("Something wrong with detailed zip refresh");
            } catch (JSONException e) {
                System.out.println("Something wrong with detailed zip refresh, json exception");
            }
        }
    }

}
