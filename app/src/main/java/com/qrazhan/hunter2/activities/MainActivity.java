package com.qrazhan.hunter2.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.activities.fragments.BrowsingFragment;
import com.qrazhan.hunter2.Constants;
import com.qrazhan.hunter2.activities.fragments.NavigationDrawerFragment;
import com.qrazhan.hunter2.R;

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Fragment mFragment;

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
                            ((BrowsingFragment) mFragment).refresh(getApplicationContext());
                        }
                    }
                });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        mFragment = BrowsingFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
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
                ((BrowsingFragment) mFragment).refresh(getApplicationContext());
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
