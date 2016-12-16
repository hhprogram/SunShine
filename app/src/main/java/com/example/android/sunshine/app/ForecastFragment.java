package com.example.android.sunshine.app;

/**
 * Created by harrison on 11/16/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayList<String> days;
    ArrayAdapter<String> adapter;
    GetRequest request;
    String forecastJson;
    JSONArray week = null;
    final static String LIST_DATA = "LIST_DATA";
    public ForecastFragment() {
    }

    //just like the onCreate method in MainActivity, this is called when this particular fragment
    //is created/inflated. It calls its super class constructor along with denoting that
    //this fragment has an OptionsMenu. This line tells it to class the onCreateOptionsMenu method
    //within this fragment so it knows what menu XML to get to populate the menu
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //LIST_ITEM_FORECAST is the layout XML file where the listed forecat textViews reside.
        //LISTVIEW_FORECAST_ITEM is where the actual individual text view for each list item
        //is referred to. DAYS is the array of actual data that the adapter will use
        //to populate each view of ID LISTVIEW_FORECAST_ITEM
        //REQUEST is the asynctask object used to create a new thread to do the background work
        days = new ArrayList<String>();
        request = new GetRequest();
    }

    //called when setHasOptionMenu is set to True. we refer to the forecastfragment as we
    //specifically made a seperate menu XML to handle the forecast fragment page
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Goes to the RES folder and then the MENU subfolder and looks for the XML files with
        //the ID(file name) of  FORECASTFRAGMENT. We specifically made FORECASTFRAGMENT as the
        //new menu XML with the added refresh button
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    //returns boolean if the id in the options bar clicked is ACTION_REFRESH
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //this method handles if any items in our MENU layout is clicked. Don't need a
        //onClick tag in the XML like you do for normal layouts
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement, if the id of my ITEM is ACTION_REFRESH then go
        //into this code block
        if (id == R.id.action_refresh) {
            //this makes a toast when ID is ACTION_REFRESH. getActivity gets the current context
            //we are in (Fragment), GETITLE gets the title of the ITEM that was selected
            Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
            try {
                //call a private helper method that actually calls the network connection to get
                //the weather data
                GetRequest refreshRequest = new GetRequest();
                //Needed to add this CLEAR line as before didn't have it and my listview just kept
                //getting longer and longer with subsequent refresh calls because I never emptied out
                //the previous data
                adapter.clear();
                updateAdapter(refreshRequest.execute("94025").get(), "94025");
                //Actually don't need these lines below as built into the Android source code for
                //ArrayAdapters is whenever it changes data it calls a method notifydatasetchanged
                //which takes the new dataset in the array adapater and updates the view for you
//                View root = getView();
//                ListView listview = (ListView) root.findViewById(R.id.listview_forecast);
//                listview.setAdapter(adapter);
            } catch (ExecutionException e) {
                System.out.println("Execution exception thrown from get request");
                return false;
            }catch (InterruptedException e) {
                 System.out.println("Interrupted exception thrown from get request");
                return false;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //ArrayList for the ArrayAdapter to know what strings to put in each View

        try {
            //execute the GetRequest AsyncTask object, with the given parameters (the
            //important one is the FORECAST which is the API URL
            //then we call get() to actual return the result of our 'execute' call
            forecastJson = request.execute("94704").get();
            //get() throws both these exception so I need to catch them or else Java will
            //not allow me to compile because this is a compile time error
        } catch (ExecutionException e) {
            System.out.println("Execution exception thrown from get request");
            return null;
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception thrown from get request");
            return null;
        }
        updateAdapter(forecastJson, "94704");

//            ArrayList<String> days = new ArrayList<String>();
//            days.add("Sunday");
//            days.add("Monday");
//            days.add("Tuesday");
//            days.add("Wednesday");


        //searches for a ViewID (ie any VIEW object) with ID LISTVIEW_FORECAST).
        //Uses the root View/layout ROOTVIEW as the root of the tree of layouts / views in which
        //we search for our View ID
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        //This gets our current context then goes down the hierarchy tree until it can find
        //a view with the unique ID of FRAGMENTFRAME
        listView.setAdapter(adapter);
        //sets a listener to this fragment activity. So if any item on this activity is clicked
        //then it uses the new object onItemClickListener shown in the parameter and calls the
        //onItemClick method that must be overridden when creating a new OnItemClickListener.
        //What we do right below is instantiate this new instance of OnItemClickListener 'in-place'
        //to make it simpler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i , long l){
                //just a toast to make sure that the onItemClickListener is doing the right thing
                if (i == 0) {
                    Toast.makeText(getActivity(),"No detail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), adapter.getItem(i), Toast.LENGTH_SHORT).show();
                    Intent detail = new Intent(getActivity(), DetailActivity.class);
                    JSONObject detailToSend = null;
                    try {
                        detailToSend = week.getJSONObject(i - 1);
                    } catch (JSONException e) {
                        System.out.println("Error On the Click");
                    }
                    detail.putExtra(LIST_DATA, detailToSend.toString());
                    startActivity(detail);
                }
            }
        });
        return rootView;
    }

    //private helper class that takes in a JSON formatted string and then parses it to update
    //the array adapter to refresh with the latest data, CODE is the postal code that we are
    //using to update the array adapter data
    private void updateAdapter(String rawJson, String code) {
        JSONObject obj, day, weather;
        String weatherDescription = null, dayResult = null;
        Double temp = 0.0;
        long unixSeconds;

        try {
            //we can construct JSONobjects by feeding in a string  that is in JSON format.
            //FORECASTJSON is in JSON format because that is the API response we got just
            //converted to a string by GetRequest.java file. Convert to JSONObject as these
            //have methods that can easily retrieve the dictionaries that JSON returns
            obj = new JSONObject(rawJson);
            //returns an array that is associated with the key LIST. As LIST is a key for
            //a list of dictionaries for each day of the week
            week = obj.getJSONArray("list");
        } catch (JSONException e) {
            System.out.println("JSON Exception occurred");
        }
        if (week.length() == 0){
            System.out.println("Nothing in week");
        }
        days.add(code);
        for (int i = 0; i < week.length(); i++) {
            try {
                //loop through each index of the array, as index 0 is the first day, 1 is the
                //next day in forecast etc...
                day = week.getJSONObject(i);
                //each index of the array itself stores a dictionary (which is considered a
                //JSONObject). And the WEATHER key links to another list.
                //and then this array actually only has one element so just get the first
                //element which is 0 which is a dictionary (ie a JSONObject)
                weather = day.getJSONArray("weather").getJSONObject(0);
                //then we want the string that is associated with the key "description in the
                //JSONObject Weather
                weatherDescription = weather.getString("description");
                //gets the dictionary that is associated with the key TEMP, and then within that
                //dictionary we get the double value of the temperature that is associated with
                //the key MAX
                temp = day.getJSONObject("temp").getDouble("max");
                temp = temp*9.0/5.0 + 32.00;
                //Getting the date of the forecasted weather data (this is returned in UNIX seconds
                //from the EPOCH
                unixSeconds = day.getLong("dt");
                //multiply by 1000 to convert to milliseconds because that is the units of time
                //that Date constructor takes
                Date date = new Date(unixSeconds*1000L);
                //See SimpleDateFormat Java Docs for this construction
                SimpleDateFormat sdf = new SimpleDateFormat("M-d-yy EE");
                //see the parent class fo SimpleDateFormat class DateFormat for this method...
                //takes in a Date object and returns a string
                dayResult = sdf.format(date);
            } catch (JSONException e) {
                System.out.println("JSON Exception occurred");
            }
            //concatenate the weather description and temperature into one element in the array
            //as want to keep all data about one day in the same view (ie that means must
            //be in same element of Array as the arrayAdapter just iterates through given
            //array and each element gets its own view
            days.add(dayResult + " " + weatherDescription + " " + temp.toString());
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.list_item_forecast, R.id.listview_forecast_item, days);

        }


    }
}