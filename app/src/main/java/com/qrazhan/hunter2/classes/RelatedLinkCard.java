package com.qrazhan.hunter2.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by prashan on 8/24/14.
 */
public class RelatedLinkCard extends Card {

    private RelatedLink link;

    /**
     * Constructor with a custom inner layout
     * @param context
     */
    public RelatedLinkCard(Context context, RelatedLink link) {
        this(context, R.layout.related_link_card_layout, link);
    }

    /**
     *
     * @param context
     * @param innerLayout
     */
    public RelatedLinkCard(Context context, int innerLayout, RelatedLink link) {
        super(context, innerLayout);
        this.link = link;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView title = (TextView) parent.findViewById(R.id.related_link_title);
        TextView domain = (TextView) parent.findViewById(R.id.related_link_domain);
        ImageView fav = (ImageView) parent.findViewById(R.id.related_link_image);

        if(fav != null){
            Ion.with(fav).load(link.favicon);
        }
        if(title != null)
            title.setText(link.title);
        if(domain != null)
            domain.setText("("+link.domain+")");
    }
}
