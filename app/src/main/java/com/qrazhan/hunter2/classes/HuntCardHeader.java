package com.qrazhan.hunter2.classes;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qrazhan.hunter2.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by prashan on 8/9/14.
 */
public class HuntCardHeader extends CardHeader {

    private Hunt hunt;

    public HuntCardHeader(Context context, Hunt hunt) {
        super(context, R.layout.custom_header_inner_layout);
        this.hunt = hunt;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView title = (TextView) parent.findViewById(R.id.hunt_title);

        String html = "<b>"+hunt.title+"</b>"+" - "+hunt.subtitle;

        if(title != null)
            title.setText(Html.fromHtml(html));
    }
}
