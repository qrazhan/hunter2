package com.qrazhan.hunter2.classes;

import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by prashan on 8/24/14.
 */
public class RelatedLink {

    public String url;
    public String title;
    public String domain;
    public String favicon;

    public RelatedLink(String url, String title, String domain, String favicon){
        this.url = url;
        this.title = title;
        this.domain = domain;
        this.favicon = favicon;
    }

    public RelatedLink(JsonObject obj){
        this(obj.get("url").getAsString(),
                obj.get("title").getAsString(),
                obj.get("domain").getAsString(),
                obj.get("favicon").getAsString());
    }
}
