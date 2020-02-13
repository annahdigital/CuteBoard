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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuteboard.Adapters.PostAdapter;
import com.example.cuteboard.DatabaseWork.RSSDatabase;
import com.example.cuteboard.DatabaseWork.RSSDatabaseBuilder;
import com.example.cuteboard.Models.RSSPost;
import com.example.cuteboard.Network.NetworkStateReader;
import com.example.cuteboard.Network.NetworkStateReceiver;
import com.example.cuteboard.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
            showError();
            new CacheLoadingTask(this).execute();
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
               new RSSFeedControl(this, RSS).execute();
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

    private static class CacheLoadingTask  extends AsyncTask<Void, Void, Boolean> {

        private ArrayList<RSSPost> loaded_posts;
        private final WeakReference<MainActivity> activityReference;

        private CacheLoadingTask(MainActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {

            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                MainActivity activity = activityReference.get();
                if (activity == null || activity.isFinishing()) return false;
                loaded_posts = new ArrayList<>();
                loaded_posts.addAll(activity.db.getRSSPostDao().getAll());
                return loaded_posts.size() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.mSwipeLayout.setRefreshing(false);
            if (success) {
                activity.mRecyclerView.setAdapter(new PostAdapter(activity, loaded_posts));
            } else showError(activity);

        }

        private void showError(MainActivity context) {
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

    private static class RSSFeedControl extends AsyncTask<Void, Integer, Boolean>
    {

        private final WeakReference<MainActivity> activityReference;
        private final String address;
        private ArrayList<RSSPost> loaded_posts;
        private final ArrayList<WebView> webViews = new ArrayList<>();

        RSSFeedControl(MainActivity context, String address)
        {
            this.activityReference = new WeakReference<>(context);
            this.address = address;
        }

        @Override
        protected void onPreExecute() {

            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected void onProgressUpdate(Integer...integers)
        {
            final MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            super.onProgressUpdate(integers);
            final int i = integers[0];
            WebView view = new WebView(activity);
            view.setWebViewClient(new WebViewClient()
            {
                @Override
                public void onPageFinished(WebView view, String url)
                {
                    view.saveWebArchive(activity.getFilesDir().getAbsolutePath() + File.separator + i + ".mht");
                }
            });
            view.loadUrl(loaded_posts.get(i).getLink());
            webViews.add(view);
            if (i == 9)
                showMessage(activity);
        }


        @Override
        protected Boolean doInBackground(Void...voids) {
            try {
                MainActivity activity = activityReference.get();
                if (activity == null || activity.isFinishing()) return false;
                ProcessXml(tryConnection(address, activity), activity.db);
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.mSwipeLayout.setRefreshing(false);
            if (success)
            {
                activity.mRecyclerView.setAdapter(new PostAdapter(activity, loaded_posts));
            }
            else showError(address, activity);

        }

        private Document tryConnection(String address, MainActivity context)
        {
            try {
                if(!address.startsWith("http://") && !address.startsWith("https://"))
                    address = "http://" + address;
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                return builder.parse(inputStream);
            }
            catch (Exception e) {
                e.printStackTrace();
                showError(address, context);
                return null;
            }
        }

        private void ProcessXml(Document document, RSSDatabase db) {
            ArrayList<RSSPost> posts = new ArrayList<>();
            if (document != null) {
                Element root = document.getDocumentElement();
                Node channel = root.getChildNodes().item(1);
                if (channel == null)
                    channel = root.getChildNodes().item(0);
                NodeList items = channel.getChildNodes();
                for (int i = 0; i < items.getLength(); i++) {
                    Node currentItem = items.item(i);
                    if (currentItem.getNodeName().equalsIgnoreCase("item")) {
                        RSSPost rssPost = new RSSPost();
                        NodeList itemchilds = currentItem.getChildNodes();
                        for (int j = 0; j < itemchilds.getLength(); j++) {
                            Node current = itemchilds.item(j);
                            if (current.getNodeName().equalsIgnoreCase("title")) {
                                rssPost.setTitle(current.getTextContent());
                            }
                            else if (current.getNodeName().equalsIgnoreCase("description")) {
                                String[] s = current.getTextContent().split(">");
                                StringBuilder desc = new StringBuilder();
                                for (String value : s)
                                    if (value.charAt(0) != '<' && value.length() > 6 && !value.contains("<img")) {
                                        String[] subs = value.split("<");
                                        for (String subss : subs)
                                            if (subss.length() > 3 && !subss.contains("a href") && !subss.contains("br clear"))
                                                desc.append(subss);
                                    }
                                //rssPost.setContent(current.getTextContent());
                                rssPost.setContent(desc.toString());
                            }
                            else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                                try {
                                    SimpleDateFormat dt = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                                    SimpleDateFormat dt1 = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z");
                                    Date date = dt1.parse(current.getTextContent());
                                    rssPost.setDate(dt.format(Objects.requireNonNull(date)));
                                }
                                catch (java.text.ParseException e)
                                {
                                    e.printStackTrace();
                                    rssPost.setDate(current.getTextContent());
                                }
                            }
                            else if (current.getNodeName().equalsIgnoreCase("link")) {
                                rssPost.setLink(current.getTextContent());
                            }
                            else if (current.getNodeName().equalsIgnoreCase("media:thumbnail") ||
                                    current.getNodeName().equalsIgnoreCase("enclosure")) {
                                rssPost.setImage(current.getAttributes().item(0).getTextContent());
                            }
                        }
                        posts.add(rssPost);
                    }
                }
                loaded_posts = posts;
                db.getRSSPostDao().deleteAll();
                int count = 10;
                if (posts.size() < count) count = posts.size();
                for (int k = 0; k < count; k++) {
                    RSSPost post = posts.get(k);
                    String img= post.getImage();
                    try {
                        URL url = new URL(img);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        post.setCachedImage(encodedImage);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    db.getRSSPostDao().insert(post);
                    publishProgress(posts.indexOf(post));
                }
            }
        }

        private void showError(String address, MainActivity context) {
            String message = "Can't connect to " + address;
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

        private void showMessage(MainActivity context) {
            String message = "Now you have some cached news!";
            Toast toast = Toast.makeText(context,
                    message,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastContainer = (LinearLayout) toast.getView();
            ImageView catImageView = new ImageView(context);
            catImageView.setImageResource(R.drawable.idea);
            toastContainer.addView(catImageView, 0);
            toast.show();
        }

    }
}
