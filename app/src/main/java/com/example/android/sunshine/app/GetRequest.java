package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by harrison on 11/11/16.
 */

//Need this class as need to use a different thread to make a network connection. Not allowed
    //to use the main thread (need to elaborate on this more on the AsyncTask thing)

public class GetRequest extends AsyncTask<String, Void, String> {
    //make a static string so that I can refer to it in MAINACTIVITY.JAVA to feed to call
    //put this into a browser to see what the JSON response looks like
    static String FORECAST = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94704&mode=json&units=metric&cnt=7&APPID=5a295e974ddcf5acfb71d434425a7fd0";
    //making fixed sized arrays for the query parameters. First element is the query key and 2nd
    //is the query parameter value
    final String[] units = {"units","metric"};
    final String[] apiKey = {"APPID","5a295e974ddcf5acfb71d434425a7fd0"};
    String[] location = {"q", ""};
    //any class that extends the AsyncTask must implement the doInBackground method. This is the
    //method that actually executes that task with parameters <String, Void,String>, the first
    //parameter is the type that will be passed into the EXECUTE method from the UI thread (see
    // forecast fragment file),
    //the 2nd parameter is the type of object you get when you call progress update methods (Void
    //for this because have not implemented any progress update methods)
    //3rd parameter is the type of object returned in the postExecute method (type of object
    //sent back to the UI thread
    //the result of this method is returned in the
    //"forecastJson = request.execute(GetRequest.FORECAST, null, null).get();" line in
    //MainActivity.java file. .get() gets the result from this method. Just the .execute() returns
    //an AsyncTask object not the actual return value of executing the AsyncTask object
    //If i wanted to not use GET() then i would need to implement postExecute() method in
    // this GetRequest class that would return the actual value of downloadContent method
    @Override
    protected String doInBackground(String... params) {
        //do your request in here so that you don't interrupt the UI thread
        try {
            return downloadContent(params[0]);
        } catch (IOException e) {
            return "Unable to retrieve data. URL may be invalid.";
        }
    }

    //a private helper class that does the actual network connection. Called when we execute
    //an instance of this GetRequest class. Accesses the WeatherMap API and returns a string
    //that is the JSON API response
    private String downloadContent(String postalCode) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferReader = null;
        String forecastJson = null;
        //Use URL class to help build a URL to make it more dynamic vs using a static string. Calls
        //a private helper class to do the actual building of the URL. Currently just the postal
        //code is dynamic
        URL url = new URL(buildURL(postalCode));
//        System.out.println(url.toString());
//        System.out.println(postalCode);
        //Connects to website and uses the URL that we built to access
        urlConnection = (HttpURLConnection) url.openConnection();
        //sets the type of connection as a GET request
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        //uses the URLCONNECTION object that connected to our built URL and then whatever the get
        //request got back we make an input stream INPUT so that we can go through it
        InputStream input = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (input == null) {
            System.out.println("The initial input stream didn't work");
            return null;
        }
        bufferReader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = bufferReader.readLine()) != null) {
            buffer.append(line + "\n");
        }
        if (buffer.length() == 0) {
            return null;
        }
        return buffer.toString();
    }


    //helper method to build the API URL based on the customizable input postalCode and then the rest
    //of the forecast data query parameters that are fixed
    private String buildURL(String postalCode) {
        location[1] = postalCode;
        Uri.Builder build = new Uri.Builder();
        build.scheme("http");
        build.authority("api.openweathermap.org");
        build.appendPath("data");
        build.appendPath("2.5");
        build.appendPath("forecast");
        build.appendPath("daily");
        build.appendQueryParameter(location[0],location[1]);
        build.appendQueryParameter(units[0],units[1]);
        build.appendQueryParameter(apiKey[0],apiKey[1]);
        return build.toString();
    }
}
