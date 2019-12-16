package com.example.cuteboard.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuteboard.DatabaseWork.RSSDatabase;
import com.example.cuteboard.DatabaseWork.RSSDatabaseBuilder;
import com.example.cuteboard.Network.NetworkStateReader;
import com.example.cuteboard.Network.NetworkStateReceiver;
import com.example.cuteboard.R;
import com.example.cuteboard.Tasks.CacheLoadingTask;
import com.example.cuteboard.Tasks.RSSFeedControl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RSSDatabase db;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private PopupWindow mPopupWindow;
    private SharedPreferences sharedPref;
    private BroadcastReceiver mNetworkReceiver;
    private final String[] links = new String[] { "https://www.rt.com/rss/",
                                            "https://tech.onliner.by/feed",
                                            "https://people.onliner.by/feed",
                                            "https://news.tut.by/rss/index.rss"};

    private String RSS;
    private static final String APP_RSS = "RSS";
    private static final String APP_PREFERENCES = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = RSSDatabaseBuilder.getInstance(this);
        mNetworkReceiver = new NetworkStateReceiver();

        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        TextView toolbarURL= this.findViewById(R.id.current_url);
        toolbarURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();
            }
        });

        // setting up recycler view for feed
        mRecyclerView = findViewById(R.id.post_view);
        mSwipeLayout = findViewById(R.id.swipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
        FloatingActionButton addUrlButton = this.findViewById(R.id.settings_rss);
        addUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();
            }
        });
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts();
            }
        });

        // getting url of the rss feed
        sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        loadPosts();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // registering network receiver to track network state
        this.registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    // dialog for entering a URL link
   private void showPopUp()
   {
       // hide button at the background
       final FloatingActionButton settingsButton = this.findViewById(R.id.settings_rss);
       settingsButton.setVisibility(View.GONE);
       mRecyclerView.setAlpha(0);
       Context mContext = getApplicationContext();
       // popup window for entering rss
       LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
       View customView = Objects.requireNonNull(inflater).inflate(R.layout.start_page, null);
       mPopupWindow = new PopupWindow(
               customView,
               LayoutParams.WRAP_CONTENT,
               LayoutParams.WRAP_CONTENT,
               true
       );

       // showing button at the background
       mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
           @Override
           public void onDismiss() {
               settingsButton.setVisibility(View.VISIBLE);
               mRecyclerView.setAlpha(1);
           }
       });
       mPopupWindow.setElevation(5.0f);
       findViewById(R.id.main_space).post(new Runnable() {
           @Override
           public void run() {
               mPopupWindow.showAtLocation(findViewById(R.id.main_space), Gravity.CENTER,0,0);
           }
       });
       // reading entered url
       final EditText urlEdit = customView.findViewById(R.id.input_rss);
       Button mStartButton = customView.findViewById(R.id.get_started);
       mStartButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (!urlEdit.getText().toString().isEmpty()) {
                   mPopupWindow.dismiss();
                   SharedPreferences.Editor editor = sharedPref.edit();
                   editor.putString(APP_RSS, urlEdit.getText().toString());
                   editor.apply();
                   RSS = sharedPref.getString(APP_RSS, "");
                   loadPosts();
               }
           }
       });
       Button randomLinkButton = customView.findViewById(R.id.random_link);
       randomLinkButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                urlEdit.setText(links[new Random().nextInt(links.length)]);
           }
       });
   }


    // managing toolbar for different network states
   public void networkStateChanged(String status)
   {
           if (status.equals(getResources().getString(R.string.wifi)) || status.equals(getResources().getString(R.string.mobile_data))) {
               TextView toolbarTitle = this.findViewById(R.id.label);
               toolbarTitle.setText(getResources().getString(R.string.app_name));
               ImageView iconToolbar = this.findViewById(R.id.icon_toolbar);
               iconToolbar.setImageResource(R.drawable.kitty);
           } else {
               TextView toolbarTitle = this.findViewById(R.id.label);
               toolbarTitle.setText(getResources().getString(R.string.no_internet_short));
               ImageView iconToolbar = this.findViewById(R.id.icon_toolbar);
               iconToolbar.setImageResource(R.mipmap.kitty_disappointed);
           }
   }

   private void loadPosts()
   {
       if (NetworkStateReader.getConnectivityStatusString(this).equals(getResources().getString(R.string.no_internet)))
       {
            mSwipeLayout.setRefreshing(false);
            showError();
            new CacheLoadingTask(db, this, mRecyclerView, mSwipeLayout).execute();
       }
       else {
           if (!sharedPref.contains(APP_RSS)) {
               showPopUp();
           }
           else
           {
               RSS = sharedPref.getString(APP_RSS, "");
               TextView toolbarURL= this.findViewById(R.id.current_url);
               toolbarURL.setText(RSS.replace("https://", ""));
               new RSSFeedControl(this, RSS, mSwipeLayout, mRecyclerView, db).execute();
           }
       }
   }

    private void showError() {
        String message = "No internet connection, but there is some cached posts!";
        Toast toast = Toast.makeText(this,
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(this);
        catImageView.setImageResource(R.drawable.kitty_wow);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }


}
