package com.qrazhan.hunter2.views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.qrazhan.hunter2.R;
import com.qrazhan.hunter2.classes.Comment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by prashan on 8/14/14.
 */
public class CommentView extends FrameLayout {

    @InjectView(R.id.comment_author)
    TextView author;
    @InjectView(R.id.comment_username)
    TextView username;
    @InjectView(R.id.comment_maker)
    TextView maker;
    @InjectView(R.id.comment_body)
    TextView body;
    @InjectView(R.id.comment_user_pic)
    ImageView pic;

    public Comment comment;

    public CommentView(Context context, Comment comment) {
        super(context);
        this.comment = comment;
    }

    public CommentView(Context context, AttributeSet attrs, Comment comment) {
        super(context, attrs);
        this.comment = comment;
    }

    public CommentView(Context context, AttributeSet attrs, int defStyle, Comment comment) {
        super(context, attrs, defStyle);
        this.comment = comment;
    }

    public void populateLayout(Activity activity){
        View view = activity.getLayoutInflater().inflate( R.layout.comment, this);
        ButterKnife.inject(this, view);
        author.setText(comment.author.name);
        username.setText("@"+comment.author.username+" - "+comment.author.headline);
        maker.setText(comment.maker ? "M" : "");
        body.setText(Html.fromHtml(comment.text));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(!sharedPref.getBoolean(getContext().getString(R.string.settings_low_res_key), false)) {
            Ion.with(pic)
                    .error(R.drawable.ic_launcher)
                    .load(comment.author.imgUrl);
        } else {
            Ion.with(pic)
                    .error(R.drawable.ic_launcher)
                    .load(comment.author.smallImgUrl);
        }
    }
}
