package com.qrazhan.hunter2.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.activities.fragments.BrowsingFragment;
import com.qrazhan.hunter2.Constants;
import com.qrazhan.hunter2.activities.fragments.CommentsFragment;
import com.qrazhan.hunter2.activities.fragments.HuntViewFragment;
import com.qrazhan.hunter2.R;
import com.qrazhan.hunter2.classes.Comment;
import com.qrazhan.hunter2.classes.Hunt;

public class HuntActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    public Hunt hunt;
    public HashMap<Integer, Comment> commentsMap = new HashMap<Integer, Comment>();
    private HuntViewFragment viewFragment;
    private CommentsFragment commentsFragment;
    public boolean commentsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);

        hunt = getIntent().getParcelableExtra("hunt");
        getActionBar().setTitle(hunt.title);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        Ion.with(getApplicationContext())
                .load("https://api.producthunt.com/v1/posts/"+hunt.id+"/comments")
                .setHeader("Authorization", "Bearer "+ Constants.CLIENT_TOKEN)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result != null && result.has("comments")) {
                            JsonArray comments = result.get("comments").getAsJsonArray();

                            addCommentsToMap(comments);

                            commentsLoaded = true;
                            commentsFragment.addCommentViews();
                        }
                    }
                });
    }

    public void addCommentsToMap(JsonArray comments){
        for (int i = 0; i < comments.size(); i++) {
            JsonObject obj = comments.get(i).getAsJsonObject();
            Comment comment = new Comment(obj);
            if(!commentsMap.containsKey(comment.id)) {
                commentsMap.put(comment.id, comment);
                addCommentsToMap(obj.getAsJsonArray("child_comments"));
            }

        }
    }

    public int getCommentChildLevel(Comment comment){
        int level = 0;
        Comment temp = comment;
        while(temp.parent != -1){
            temp = commentsMap.get(comment.parent);
        }
        return level;
    }

    public ArrayList<Comment> getCommentChildren(Comment comment){
        List<Integer> ids = new ArrayList<Integer>();
        ids.addAll(commentsMap.keySet());
        Collections.sort(ids);
        ArrayList<Comment> toRet = new ArrayList<Comment>();
        for(int i : ids){
            if(commentsMap.get(i).parent == comment.id){
                toRet.add(commentsMap.get(i));
            }
        }
        return toRet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hunt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_browser:
                Intent openInBrowser = new Intent(Intent.ACTION_VIEW);
                openInBrowser.setData(Uri.parse(hunt.url));
                startActivity(openInBrowser);
                break;
            case R.id.action_share_link:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, hunt.url+" via hunter2 for Android");
                i.setType("text/plain");
                startActivity(i);
                break;
            case R.id.action_share_post:
                Intent j = new Intent();
                j.setAction(Intent.ACTION_SEND);
                j.putExtra(Intent.EXTRA_TEXT, hunt.permlink+" via hunter2 for Android");
                j.setType("text/plain");
                startActivity(j);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment;
            switch(position){
                case 0:
                    viewFragment = HuntViewFragment.newInstance(hunt);
                    return viewFragment;
                case 1:
                    commentsFragment = CommentsFragment.newInstance();
                    return commentsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Hunt".toUpperCase(l);
                case 1:
                    return "Comments".toUpperCase(l);
            }
            return null;
        }
    }
}
