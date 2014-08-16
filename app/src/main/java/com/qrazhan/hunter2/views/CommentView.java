package com.qrazhan.hunter2.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qrazhan.hunter2.R;
import com.qrazhan.hunter2.classes.Comment;

/**
 * Created by dick on 8/14/14.
 */
public class CommentView extends FrameLayout {

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

    public void populateLayout(){
        inflate(getContext(), R.layout.comment, this);
        TextView author = (TextView) findViewById(R.id.comment_author);
        TextView username = (TextView) findViewById(R.id.comment_username);
        TextView maker = (TextView) findViewById(R.id.comment_maker);
        TextView body = (TextView) findViewById(R.id.comment_body);
        author.setText(comment.author.name);
        username.setText("@"+comment.author.username+" - "+comment.author.headline);
        maker.setText(comment.maker ? "M" : "");
        body.setText(Html.fromHtml(comment.text));
    }
}
