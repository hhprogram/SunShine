package com.example.android.sunshine.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for some reason need to set a content view for this activity even though i just launch
        //a fragment with its own layout. But seems to need some layout set to work/show my fragment
        //preference fragment..Note: If i have some textview on ACTIVITY_SETTINGS it actually
        //shows up under the fragment layout. If I don't have this line then the layout for the
        //preferences doesn't show up at all. Just a blank screen
        setContentView(R.layout.activity_settings);
        // Display the fragment as the main content.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        SettingsFragment mPrefsFragment = new SettingsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);
                PreferenceManager manager = getPreferenceManager();
                SharedPreferences pref = manager.getSharedPreferences();
//                System.out.println(pref.getString(getString(R.string.zip_entry),""));
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
        //add this switch clause to manually add a back button which has the resource id of the
        //constant int ANDROID.R.ID.HOME. The method Finish() is an Activity method and calls the
        //activities onDestroy() method. Think this takes me to whatever activity launched this
        //activity as it shuts down this activity and resumes the next activity in the stack
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
