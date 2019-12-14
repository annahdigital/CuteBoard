package com.example.cuteboard.Tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.cuteboard.Adapters.PostAdapter;
import com.example.cuteboard.Models.CroppedDrawable;
import com.example.cuteboard.R;

import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String link;
    private PostAdapter.RSSPostViewHolder holder;
    private Activity context;

    public ImageLoadTask(String url, PostAdapter.RSSPostViewHolder holder, Activity context)
    {
        this.link = url;
        this.holder = holder;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void...voids)
    {
        try {
            URL url = new URL(link);
            return BitmapFactory.decodeStream(url.openStream());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        if (bitmap != null) {
            //RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
            //dr.setCircular(true);
            CroppedDrawable cd = new CroppedDrawable(bitmap);
            holder.postImageView.setImageDrawable(cd);
        }
        else
            holder.postImageView.setImageResource(R.mipmap.kitty);
    }
}


