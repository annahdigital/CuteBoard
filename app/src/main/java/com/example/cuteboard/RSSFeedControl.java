package com.example.cuteboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RSSFeedControl extends AsyncTask<Void, Void, Boolean>
{

    private HttpURLConnection connection;
    private Context context;
    private String address;
    private SwipeRefreshLayout mSwipeLayout;
    private ArrayList<RSSPost> loaded_posts;
    private RecyclerView mRecyclerView;

    public RSSFeedControl(Context mcontext, String address, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView)
    {
        this.context = mcontext;
        this.address = address;
        this.mSwipeLayout = swipeRefreshLayout;
        this.mRecyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        mSwipeLayout.setRefreshing(true);
    }


    @Override
    protected Boolean doInBackground(Void...voids) {
        /*if (online)
            return ProcessXml(GetData(params[0]));
        else
            return GetCache();*/
        try {
            ProcessXml(tryConnection(address));
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
        mSwipeLayout.setRefreshing(false);
        if (success)
        {
            mRecyclerView.setAdapter(new PostAdapter(context, loaded_posts));
        }
        else showError(address);

    }

    private Document tryConnection(String address)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            if(!address.startsWith("http://") && !address.startsWith("https://"))
                address = "http://" + address;
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(inputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
            showError(address);
            return null;
        }
    }

    private void ProcessXml(Document document) {
        ArrayList<RSSPost> posts = new ArrayList<>();

        if (document != null) {
            Element root = document.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            if (channel == null)
                channel = root.getChildNodes().item(0);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node currentchild = items.item(i);
                if (currentchild.getNodeName().equalsIgnoreCase("item")) {
                    RSSPost rssPost = new RSSPost();
                    NodeList itemchilds = currentchild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node current = itemchilds.item(j);
                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            rssPost.setTitle(current.getTextContent());
                        }
                        else if (current.getNodeName().equalsIgnoreCase("description")) {
                            String[] s = current.getTextContent().split(">");
                            String desc = "";
                            for (int ss = 0; ss < s.length; ss++)
                                if (s[ss].charAt(0) != '<' && s[ss].length() > 6 && !s[ss].contains("<img")) {
                                    String[] subs = s[ss].split("<");
                                    for (String subss : subs)
                                        if (subss.length() > 3 && !subss.contains("a href"))
                                            desc += subss;
                                }
                            //rssPost.setContent(current.getTextContent());
                            rssPost.setContent(desc);
                        }
                        else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                            try {
                                SimpleDateFormat dt = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                                SimpleDateFormat dt1 = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z");
                                Date date = dt1.parse(current.getTextContent());
                                rssPost.setDate(dt.format(date));
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
        }
        loaded_posts = posts;
    }

    private void showError(String address) {
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

}
