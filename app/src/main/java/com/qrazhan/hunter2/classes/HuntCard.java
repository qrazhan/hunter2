package com.qrazhan.hunter2.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by prashan on 8/9/14.
 */
public class HuntCard extends Card {

    private Hunt hunt;

    /**
     * Constructor with a custom inner layout
     * @param context
     */
    public HuntCard(Context context, Hunt hunt) {
        this(context, R.layout.custom_card_inner_layout, hunt);
    }

    /**
     *
     * @param context
     * @param innerLayout
     */
    public HuntCard(Context context, int innerLayout, Hunt hunt) {
        super(context, innerLayout);
        this.hunt = hunt;
        init();
    }

    /**
     * Init
     */
    private void init(){
        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getContext(), "Click Listener card=", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView upvotes = (TextView) parent.findViewById(R.id.hunt_upvotes);
        TextView numComments = (TextView) parent.findViewById(R.id.hunt_num_comments);
        TextView maker = (TextView) parent.findViewById(R.id.hunt_maker_in);
        ImageView preview = (ImageView) parent.findViewById(R.id.hunt_preview_img);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(preview != null){
            if(!sharedPref.getBoolean(getContext().getString(R.string.settings_small_card_key), false)) {
                if(!sharedPref.getBoolean(getContext().getString(R.string.settings_low_res_key), false)) {
                    Ion.with(preview)
                            .load(hunt.imgUrl);
                } else {
                    Ion.with(preview)
                            .load(hunt.smallImgUrl);
                }
            } else {
                preview.setVisibility(View.GONE);
            }
        }
        if(upvotes != null)
            upvotes.setText(hunt.upvotes+" points");
        if(numComments != null)
            numComments.setText(hunt.numComments+" comments");
        if(maker != null) {
            if(hunt.makerJoined) {
                maker.setText("M");
            } else {
                maker.setText("");
            }
        }
    }
}
