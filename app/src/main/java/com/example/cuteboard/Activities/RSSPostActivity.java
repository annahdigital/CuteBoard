package com.example.cuteboard.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.cuteboard.R;

public class RSSPostActivity extends AppCompatActivity {

    private WebView webView;
    private String link;
    private ProgressBar progressBar;
    private int postIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_web_view);
        link = getIntent().getStringExtra("link");
        postIndex = getIntent().getIntExtra("position", 0);
        webView = findViewById(R.id.post_veb_view_holder);
        progressBar = findViewById(R.id.postProgressBar);
        openURL();
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
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(link);

    }
}
