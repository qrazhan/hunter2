package com.qrazhan.hunter2.classes;

import com.google.gson.JsonObject;

/**
 * Created by prashan on 8/14/14.
 */
public class Comment {

    public int id;
    public int parent;
    public User author;
    public String text;
    public boolean maker;

    public Comment(int id, int parent, User author, String text, boolean maker){
        this.id = id;
        this.parent = parent;
        this.author = author;
        this.text = text;
        this.maker = maker;
    }

    public Comment(JsonObject json){
        this(json.get("id").getAsInt(),
                json.get("parent_comment_id").isJsonNull() ? -1 : json.get("parent_comment_id").getAsInt(),
                new User(json.get("user").getAsJsonObject()),
                json.get("body").getAsString(),
                json.get("maker").getAsBoolean());
    }
}
