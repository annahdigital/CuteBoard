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
        // + ADD IMAGE
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /*public PostAdapter(Context context, int textViewResourceId,
                       ArrayList<RSSPost> postList) {
        super(context, textViewResourceId, postList);
        myContext = (Activity) context;
        posts = postList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.post, null);

            viewHolder = new ViewHolder();
            viewHolder.postImageView = convertView.findViewById(R.id.postImage);
            viewHolder.postTitleView = convertView.findViewById(R.id.postTitleLabel);
            viewHolder.postDateView =  convertView.findViewById(R.id.postDateLabel);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RSSPost post = posts.get(position);

        viewHolder.postImageView.setImageResource(R.drawable.idea);

        if (post.getImage() != null) {
            viewHolder.postURL = post.getImage();
            new LoadImageTask().execute(viewHolder);
        }

        viewHolder.postTitleView.setText(post.getTitle());
        viewHolder.postDateView.setText(post.getDate());

        return convertView;
    }

    private class LoadImageTask extends AsyncTask<ViewHolder, Void, ViewHolder> {

        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            ViewHolder viewHolder = params[0];
            try {
                URL imageURL = new URL(viewHolder.postURL);
                viewHolder.bitmapImage = BitmapFactory.decodeStream(imageURL.openStream());
            } catch (IOException e) {
                Log.e("error", "Can't load image! :< ");
                viewHolder.postImageView = null;
            }

            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {
            if (result.bitmapImage != null) {
                result.postImageView.setImageBitmap(result.bitmapImage);
            }
        }
    }*/
}
