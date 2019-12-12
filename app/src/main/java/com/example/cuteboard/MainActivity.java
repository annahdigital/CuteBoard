package com.example.cuteboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private PopupWindow mPopupWindow;
    private Context mContext;
    private SharedPreferences sharedPref;

    private String RSS;
    public static final String APP_RSS = "RSS";
    public static final String APP_PREFERENCES = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (!sharedPref.contains(APP_RSS)) {
            showPopUp();
        }
        else
        {
            RSS = sharedPref.getString(APP_RSS, "");
            //getRssData();
        }
        
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

            }
        });
    }

   private void showPopUp()
   {
       final FloatingActionButton addButton = this.findViewById(R.id.settings_rss);
       addButton.setVisibility(View.GONE);
       mContext = getApplicationContext();
       LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
       View customView = inflater.inflate(R.layout.start_page, null);
       mPopupWindow = new PopupWindow(
               customView,
               LayoutParams.WRAP_CONTENT,
               LayoutParams.WRAP_CONTENT,
               true
       );
       mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
           @Override
           public void onDismiss() {
               addButton.setVisibility(View.VISIBLE);
           }
       });
       mPopupWindow.setElevation(5.0f);
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
               }
           }
       });
       findViewById(R.id.main_space).post(new Runnable() {
           @Override
           public void run() {
               mPopupWindow.showAtLocation(findViewById(R.id.main_space), Gravity.CENTER,0,0);
           }
       });
   }
}
