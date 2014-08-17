package com.qrazhan.hunter2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.DatePicker;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.activities.fragments.BrowsingFragment;
import com.qrazhan.hunter2.Constants;
import com.qrazhan.hunter2.activities.fragments.NavigationDrawerFragment;
import com.qrazhan.hunter2.R;

import java.util.Calendar;

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private BrowsingFragment browsingFragment;
    private String dateString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = "Today's Hunts";

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        JsonObject json = new JsonObject();
        json.addProperty("client_id", Constants.API_CLIENT_ID);
        json.addProperty("client_secret", Constants.API_CLIENT_SECRET);
        json.addProperty("grant_type", "client_credentials");

        Ion.with(getApplicationContext())
                .load("https://api.producthunt.com/v1/oauth/token")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result != null && result.has("access_token")) {
                            Constants.CLIENT_TOKEN = result.get("access_token").getAsString();
                            Constants.TOKEN_EXPIRES = System.currentTimeMillis() + ((long)result.get("expires_in").getAsInt())*1000l;
                            browsingFragment.refresh(getApplicationContext());
                        }
                    }
                });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        browsingFragment = BrowsingFragment.newInstance(dateString);
        fragmentManager.beginTransaction()
                .replace(R.id.container, browsingFragment)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_refresh:
                browsingFragment.refresh(getApplicationContext());
                break;
            case R.id.action_pick_day:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.setTitle("View products posted on...");
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++;    //what the fuck android? why only index months at 0????
            String dateString;
            dateString = year+"-";
            dateString += (month < 10 ? "0"+month : month)+"-";
            dateString += (day < 10 ? "0"+day : day)+"";
            MainActivity activity = ((MainActivity) getActivity());
            activity.browsingFragment.dateString = dateString;
            activity.dateString = dateString;
            activity.browsingFragment.refresh(activity.getApplicationContext());
            final Calendar c = Calendar.getInstance();
            if(year == c.get(Calendar.YEAR) && month-1 == c.get(Calendar.MONTH) && day == c.get(Calendar.DAY_OF_MONTH)){
                activity.getActionBar().setTitle("Today's Hunts");
            } else {
                activity.getActionBar().setTitle("Hunts for "+month+"/"+day);
            }
        }
    }
}
