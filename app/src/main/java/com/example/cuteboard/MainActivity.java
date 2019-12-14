package com.example.cuteboard;

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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private PopupWindow mPopupWindow;
    private Context mContext;
    private SharedPreferences sharedPref;
    private BroadcastReceiver mNetworkReceiver;
    private String[] links = new String[] { "https://www.rt.com/rss/",
                                            "https://tech.onliner.by/feed"};

    private String RSS;
    public static final String APP_RSS = "RSS";
    public static final String APP_PREFERENCES = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // registering network receiver to track network state
        mNetworkReceiver = new NetworkStateReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


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
                //new FetchFeedTask().execute((Void) null);
                loadPosts();
            }
        });

        // getting url of the rss feed
        sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (!sharedPref.contains(APP_RSS)) {
            showPopUp();
        }
        else
        {
            RSS = sharedPref.getString(APP_RSS, "");
            loadPosts();
            //rssFeedControl.tryConnection(RSS);
            //getRssData();
        }
    }


    // dialog for entering a URL link
   private void showPopUp()
   {
       // hide button at the background
       final FloatingActionButton settingsButton = this.findViewById(R.id.settings_rss);
       settingsButton.setVisibility(View.GONE);
       mRecyclerView.setVisibility(View.GONE);
       mContext = getApplicationContext();
       // popup window for entering rss
       LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
       View customView = inflater.inflate(R.layout.start_page, null);
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
               mRecyclerView.setVisibility(View.VISIBLE);
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
                   //getRssData();
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

   public void loadPosts()
   {
       if (NetworkStateReader.getConnectivityStatusString(this).equals(getResources().getString(R.string.no_internet)))
       {
            mSwipeLayout.setRefreshing(false);
       }
       else {
           if (!sharedPref.contains(APP_RSS)) {
               showPopUp();
           }
           else
           {
               RSS = sharedPref.getString(APP_RSS, "");
               //rssFeedControl.tryConnection(RSS);
               //getRssData();
               new RSSFeedControl(this, RSS, mSwipeLayout, mRecyclerView).execute();
           }
       }
   }


}
