package com.example.cuteboard.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuteboard.Activities.RSSPostActivity;
import com.example.cuteboard.Models.RSSPost;
import com.example.cuteboard.Network.NetworkStateReader;
import com.example.cuteboard.R;
import com.example.cuteboard.Tasks.ImageLoadTask;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RSSPostViewHolder> {

    private final Activity myContext;
    private final ArrayList<RSSPost> posts;

    static public class RSSPostViewHolder extends RecyclerView.ViewHolder{
        final TextView postTitleView;
        final TextView postDateView;
        public final ImageView postImageView;
        final TextView postContentView;

        final View rssFeedView;

        RSSPostViewHolder(View v) {
            super(v);
            rssFeedView = v;
            postDateView = v.findViewById(R.id.postDateLabel);
            postTitleView = v.findViewById(R.id.postTitleLabel);
            postImageView = v.findViewById(R.id.postImage);
            postContentView = v.findViewById(R.id.postContent);
        }
    }

    public PostAdapter(Context context,  ArrayList<RSSPost> postList) {
        myContext = (Activity) context;
        posts = postList;
    }

    @NonNull
    @Override
    public RSSPostViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        return new RSSPostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RSSPostViewHolder holder, int position) {
        final RSSPost rssFeedModel = posts.get(position);
        holder.postTitleView.setText(rssFeedModel.getTitle());
        holder.postDateView.setText(rssFeedModel.getDate());
        holder.postContentView.setText(rssFeedModel.getContent());
        //holder.postImageView.setImageResource(R.mipmap.kitty);

        if (NetworkStateReader.getConnectivityStatusString(myContext).equals(myContext.getResources().getString(R.string.no_internet)))
            new ImageLoadTask(rssFeedModel.getCachedImage(), true, holder).execute();
        else
            new ImageLoadTask(rssFeedModel.getImage(), false, holder).execute();

        holder.rssFeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(myContext, RSSPostActivity.class);
                i.putExtra("link", rssFeedModel.getLink());
                i.putExtra("position", posts.indexOf(rssFeedModel));
                myContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
