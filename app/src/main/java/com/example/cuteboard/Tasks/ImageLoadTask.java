package com.example.cuteboard.Tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.cuteboard.Adapters.PostAdapter;
import com.example.cuteboard.R;

import java.net.URL;


public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private final String link;
    private final Boolean cached;
    private final PostAdapter.RSSPostViewHolder holder;

    public ImageLoadTask(String url, Boolean cached, PostAdapter.RSSPostViewHolder holder)
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


