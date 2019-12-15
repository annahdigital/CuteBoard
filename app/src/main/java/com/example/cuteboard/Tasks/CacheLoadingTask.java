package com.example.cuteboard.Tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cuteboard.Adapters.PostAdapter;
import com.example.cuteboard.DatabaseWork.RSSDatabase;
import com.example.cuteboard.Models.RSSPost;
import com.example.cuteboard.R;

import java.util.ArrayList;

public class CacheLoadingTask  extends AsyncTask<Void, Void, Boolean> {

    private final RSSDatabase db;
    private final SwipeRefreshLayout mSwipeLayout;
    private ArrayList<RSSPost> loaded_posts;
    private final RecyclerView mRecyclerView;
    private final Activity context;

    public CacheLoadingTask(RSSDatabase db, Activity context, RecyclerView recyclerView, SwipeRefreshLayout mSwipeLayout)
    {
        this.db = db;
        this.mSwipeLayout = mSwipeLayout;
        this.context = context;
        this.mRecyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        mSwipeLayout.setRefreshing(true);
    }

    @Override
    protected Boolean doInBackground(Void...voids) {
        try {
            loaded_posts = new ArrayList<>();
            loaded_posts.addAll(db.getRSSPostDao().getAll());
            return loaded_posts.size() > 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        mSwipeLayout.setRefreshing(false);
        if (success)
        {
            mRecyclerView.setAdapter(new PostAdapter(context, loaded_posts));
        }
        else showError();

    }

    private void showError() {
        String message = "No cached news yet! Turn on internet connection.";
        Toast toast = Toast.makeText(context,
                message,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(context);
        catImageView.setImageResource(R.drawable.kitty_wow);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }


}
