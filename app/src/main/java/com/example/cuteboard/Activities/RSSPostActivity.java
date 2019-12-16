package com.example.cuteboard.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.cuteboard.Network.NetworkStateReader;
import com.example.cuteboard.Network.NetworkStateReceiver;
import com.example.cuteboard.R;

import java.io.File;

public class RSSPostActivity extends AppCompatActivity {

    private WebView webView;
    private String link;
    private ProgressBar progressBar;
    private int postIndex;
    private BroadcastReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_web_view);
        mNetworkReceiver = new NetworkStateReceiver();

        link = getIntent().getStringExtra("link");
        postIndex = getIntent().getIntExtra("position", 0);
        webView = findViewById(R.id.post_veb_view_holder);
        progressBar = findViewById(R.id.postProgressBar);

        openURL();
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
        this.registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void openURL()
    {
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (NetworkStateReader.getConnectivityStatusString(this).equals(this.getResources().getString(R.string.no_internet)))
        {
            webView.loadUrl("file://" + getFilesDir().getAbsolutePath() + File.separator + postIndex + ".mht");
        }
        else
            webView.loadUrl(link);

    }
}
