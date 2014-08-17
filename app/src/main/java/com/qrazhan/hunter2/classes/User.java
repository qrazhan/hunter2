package com.qrazhan.hunter2.classes;

import com.google.gson.JsonObject;

/**
 * Created by prashan on 8/14/14.
 */
public class User {

    public int id;
    public String name;
    public String headline;
    public String username;
    public String imgUrl;

    public User(int id, String name, String headline, String username, String imgUrl){
        this.id = id;
        this.name = name;
        this.headline = headline;
        this.username = username;
        this.imgUrl = imgUrl;
    }

    public User(JsonObject json){
        this(json.get("id").getAsInt(),
                json.get("name").isJsonNull() ? "" : json.get("name").getAsString(),
                json.get("headline").isJsonNull() ? "" : json.get("headline").getAsString(),
                json.get("username").getAsString(),
                json.get("image_url").getAsJsonObject().get("73px").getAsString());
    }
}
