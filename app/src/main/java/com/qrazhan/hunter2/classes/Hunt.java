package com.qrazhan.hunter2.classes;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.gson.JsonObject;
import com.qrazhan.hunter2.activities.HuntActivity;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by prashan on 8/9/14.
 */
public class Hunt implements Parcelable {

    public int id;
    public int upvotes;
    public String title;
    public String subtitle;
    public String permlink;
    public String url;
    public boolean makerJoined;
    public int numComments;

    public Hunt(int id, int upvotes, String title, String subtitle, String permlink, String url, boolean makerJoined, int numComments){
        this.id = id;
        this.upvotes = upvotes;
        this.title = title;
        this.subtitle = subtitle;
        this.permlink = permlink;
        this.url = url;
        this.makerJoined = makerJoined;
        this.numComments = numComments;
    }

    public Hunt(JsonObject obj){
        this(obj.get("id").getAsInt(),
                obj.get("votes_count").getAsInt(),
                obj.get("name").getAsString(),
                obj.get("tagline").getAsString(),
                obj.get("discussion_url").getAsString(),
                obj.get("redirect_url").getAsString(),
                obj.get("maker_inside").getAsBoolean(),
                obj.get("comments_count").getAsInt());
    }

    public Hunt(Parcel parcel){
        this.id = parcel.readInt();
        this.upvotes = parcel.readInt();
        this.title = parcel.readString();
        this.subtitle = parcel.readString();
        this.permlink = parcel.readString();
        this.url = parcel.readString();
        this.makerJoined = parcel.readInt() == 1;
        this.numComments = parcel.readInt();
    }

    public static Card createCardFromHunt(final Context context, final Hunt hunt){
        Card card = new HuntCard(context, hunt);
        CardHeader header = new HuntCardHeader(context, hunt);
        card.addCardHeader(header);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(context, HuntActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("hunt", hunt);
                context.startActivity(i);
            }
        });
        return card;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(upvotes);
        parcel.writeString(title);
        parcel.writeString(subtitle);
        parcel.writeString(permlink);
        parcel.writeString(url);
        parcel.writeInt(makerJoined ? 1 : 0);
        parcel.writeInt(numComments);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Hunt createFromParcel(Parcel in) { return new Hunt(in); }
        public Hunt[] newArray(int size) { return new Hunt[size]; }
    };
}
