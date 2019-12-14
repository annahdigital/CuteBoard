package com.example.cuteboard.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cuteboard.Activities.MainActivity;
import com.example.cuteboard.Activities.RSSPostActivity;
import com.example.cuteboard.Models.RSSPost;
import com.example.cuteboard.R;
import com.example.cuteboard.Tasks.ImageLoadTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RSSPostViewHolder> {

    private Activity myContext;
    private ArrayList<RSSPost> posts;

    static public class RSSPostViewHolder extends RecyclerView.ViewHolder{
        TextView postTitleView;
        TextView postDateView;
        public ImageView postImageView;
        TextView postContentView;
        String postURL;
        Bitmap bitmapImage;

        View rssFeedView;

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

        new ImageLoadTask(rssFeedModel.getImage(), holder, myContext).execute();

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
