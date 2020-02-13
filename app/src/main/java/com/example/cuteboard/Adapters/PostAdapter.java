package com.example.cuteboard.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
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

import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RSSPostViewHolder> {

    private final Activity myContext;
    private final ArrayList<RSSPost> posts;

    static class RSSPostViewHolder extends RecyclerView.ViewHolder{
        final TextView postTitleView;
        final TextView postDateView;
        final ImageView postImageView;
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


    private static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private final String link;
        private final Boolean cached;
        private final PostAdapter.RSSPostViewHolder holder;

        ImageLoadTask(String url, Boolean cached, PostAdapter.RSSPostViewHolder holder)
        {
            this.link = url;
            this.holder = holder;
            this.cached = cached;
        }

        @Override
        protected Bitmap doInBackground(Void...voids)
        {
            if (!cached) {
                try {
                    URL url = new URL(link);
                    return BitmapFactory.decodeStream(url.openStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                if (link != null) {
                    try {
                        byte[] b = Base64.decode(link, Base64.DEFAULT);
                        return BitmapFactory.decodeByteArray(b, 0, b.length);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                else return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (bitmap != null) {
                holder.postImageView.setImageBitmap(bitmap);
            }
            else
                holder.postImageView.setImageResource(R.mipmap.kitty);
        }
    }
}
