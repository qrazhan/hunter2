package com.qrazhan.hunter2.activities;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.Constants;

/**
 * Created by prashan on 8/15/14.
 */
public class BaseActivity extends Activity {

    @Override
    public void onResume(){
        super.onResume();
        if(System.currentTimeMillis() > Constants.TOKEN_EXPIRES){

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
                            }
                        }
                    });
        }
    }
}
