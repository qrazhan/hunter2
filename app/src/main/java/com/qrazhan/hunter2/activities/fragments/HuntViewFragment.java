package com.qrazhan.hunter2.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.qrazhan.hunter2.R;
import com.qrazhan.hunter2.classes.Hunt;

public class HuntViewFragment extends Fragment {

    private Hunt hunt;

    public static HuntViewFragment newInstance(Hunt hunt) {
        HuntViewFragment fragment = new HuntViewFragment();
        fragment.hunt = hunt;
        return fragment;
    }
    public HuntViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hunt, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        WebView web = (WebView) getView().findViewById(R.id.hunt_webview);
        final ProgressBar bar = (ProgressBar) getView().findViewById(R.id.web_progress);
        bar.setIndeterminate(true);
        bar.setVisibility(View.VISIBLE);
        Log.w("webView", hunt.url);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(hunt.url);
        web.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                view.setVisibility(View.VISIBLE);
                bar.setVisibility(View.GONE);
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
