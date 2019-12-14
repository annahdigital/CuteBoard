package com.example.cuteboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RSSPostViewHolder> {

    private Activity myContext;
    private ArrayList<RSSPost> posts;

    static class RSSPostViewHolder extends RecyclerView.ViewHolder{
        TextView postTitleView;
        TextView postDateView;
        ImageView postImageView;
        TextView postContentView;
        String postURL;
        Bitmap bitmapImage;

        private View rssFeedView;

        public RSSPostViewHolder(View v) {
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

    @Override
    public RSSPostViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        return new RSSPostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RSSPostViewHolder holder, int position) {
        final RSSPost rssFeedModel = posts.get(position);
        holder.postTitleView.setText(rssFeedModel.getTitle());
        holder.postDateView.setText(rssFeedModel.getDate());
        holder.postContentView.setText(rssFeedModel.getContent());
        holder.postImageView.setImageResource(R.mipmap.kitty);
        // + ADD IMAGE
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
