package com.qrazhan.hunter2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.DatePicker;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.activities.fragments.BrowsingFragment;
import com.qrazhan.hunter2.Constants;
import com.qrazhan.hunter2.activities.fragments.NavigationDrawerFragment;
import com.qrazhan.hunter2.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;


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
    private Date currDate = new Date();
    private int currPosition = -1;  //initialize to -1 so that the first call to onNavigationDrawerItemSelected() goes through

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
        if(position != currPosition) {
            FragmentManager fragmentManager = getFragmentManager();
            switch(position){
                case 0:
                    browsingFragment = BrowsingFragment.newInstance(dateString);
                    fragmentManager.beginTransaction()
                        .replace(R.id.container, browsingFragment)
                        .commit();

                    currPosition = position;
                    break;
                case 1:
                    Intent i = new Intent(this, SettingsActivity.class);
                    startActivity(i);
                    break;
            }



        }
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
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), "datePicker");

                View view = getLayoutInflater().inflate(R.layout.calendar_date_picker, null);
                final CalendarPickerView calendarPickerView = (CalendarPickerView) view.findViewById(R.id.calendar);
                final Date today = new Date();
                Calendar tmo = Calendar.getInstance();
                tmo.add(Calendar.DAY_OF_MONTH, 1);
                Calendar janFirst = Calendar.getInstance();
                janFirst.set(Calendar.MONTH, Calendar.JANUARY);
                janFirst.set(Calendar.DAY_OF_MONTH, 1);
                janFirst.set(Calendar.YEAR, 2014);
                calendarPickerView.init(janFirst.getTime(), tmo.getTime()).withSelectedDate(currDate);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view); // Set the view of the dialog to your custom layout
                builder.setTitle("View Products posted on...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd");
                        Date selected = calendarPickerView.getSelectedDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(selected);
                        Calendar current = Calendar.getInstance();
                        current.setTime(currDate);
                        if(calendar.get(Calendar.MONTH) == current.get(Calendar.MONTH)
                                && calendar.get(Calendar.DAY_OF_MONTH) == current.get(Calendar.DAY_OF_MONTH)
                                && calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR)){
                            dialog.dismiss();
                            return;
                        }
                        currDate = selected;
                        String dateString = format.format(selected);
                        browsingFragment.dateString = dateString;
                        MainActivity.this.dateString = dateString;
                        browsingFragment.refresh(getApplicationContext());

                        if(today.equals(selected)){
                            getActionBar().setTitle("Today's Hunts");
                        } else {
                            getActionBar().setTitle("Hunts for "+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH));
                        }

                        dialog.dismiss();
                    }});

                // Create and show the dialog
                builder.create().show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
