package com.qrazhan.hunter2.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.Constants;
import com.qrazhan.hunter2.R;
import com.qrazhan.hunter2.classes.Hunt;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

public class BrowsingFragment extends Fragment {

    public static BrowsingFragment newInstance() {
        BrowsingFragment fragment = new BrowsingFragment();
        return fragment;
    }

    public BrowsingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browsing, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        Context context = getActivity().getApplicationContext();
//        refresh(context);
    }

    public void refresh(final Context context){
        final ProgressBar bar = (ProgressBar) getView().findViewById(R.id.browsing_progress);
        final CardListView cardListView = (CardListView) getView().findViewById(R.id.testcard);
        cardListView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        bar.setIndeterminate(true);

        Ion.with(context)
                .load("https://api.producthunt.com/v1/posts")
                .setHeader("Authorization", "Bearer "+ Constants.CLIENT_TOKEN)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if(e != null && e instanceof TimeoutException){
                            bar.setVisibility(View.GONE);
                            Toast.makeText(context, "Request timed out.", Toast.LENGTH_SHORT);
                            return;
                        }

                        if(result != null) {
                            bar.setVisibility(View.GONE);
                            JsonArray hunts = result.getAsJsonArray("posts");
                            ArrayList<Card> cards = new ArrayList<Card>();
                            for (int i = 0; i < hunts.size(); i++) {
                                JsonObject obj = hunts.get(i).getAsJsonObject();
                                Hunt hunt = new Hunt(obj);
                                cards.add(Hunt.createCardFromHunt(context, hunt));
                            }
                            CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(context, cards);
                            cardListView.setAdapter(mCardArrayAdapter);
                            cardListView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
