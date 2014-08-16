package com.qrazhan.hunter2.activities.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qrazhan.hunter2.R;
import com.qrazhan.hunter2.activities.HuntActivity;
import com.qrazhan.hunter2.classes.Comment;
import com.qrazhan.hunter2.views.CommentView;

import java.util.ArrayList;
import java.util.Collection;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CommentsFragment extends Fragment {

    public static CommentsFragment newInstance() {
        CommentsFragment fragment = new CommentsFragment();
        return fragment;
    }

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(((HuntActivity) getActivity()).commentsLoaded){
            addCommentViews();
        }
    }

    public void addCommentViews(){
        Log.w("Comments", "addViews called");
        HuntActivity activity = (HuntActivity) getActivity();
        ArrayList<Integer> added = new ArrayList<Integer>();
        Collection<Comment> comments = activity.commentsMap.values();
        LinearLayout rootLayout = (LinearLayout) getView().findViewById(R.id.comment_root);
        for(Comment comment : comments){
            addCommentView(comment, activity, rootLayout, 0);
        }
        rootLayout.invalidate();
    }

    public void addCommentView(Comment comment, HuntActivity activity, LinearLayout rootLayout, int indent){
        CommentView view = new CommentView(activity.getApplicationContext(), comment);
        //Log.w("Comments", indent+"");

        view.populateLayout();
        rootLayout.addView(view);

        LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) view.getLayoutParams();
        params.setMargins(indent*50, 0, 0, 0); //substitute parameters for left, top, right, bottom
        view.setLayoutParams(params);


        ArrayList<Comment> children = activity.getCommentChildren(comment);
        for(Comment child : children){
            addCommentView(child, activity, rootLayout, indent+1);
        }
    }
}
