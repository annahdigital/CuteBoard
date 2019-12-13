package com.example.cuteboard;

import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;

public class NetworkStateReader {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int connectivityStatus = NetworkStateReader.getConnectivityStatus(context);

        String status = null;
        if (connectivityStatus == NetworkStateReader.TYPE_WIFI)
            status = context.getResources().getString(R.string.wifi);
        else if (connectivityStatus == NetworkStateReader.TYPE_MOBILE)
            status = context.getResources().getString(R.string.mobile_data);
        else if (connectivityStatus == NetworkStateReader.TYPE_NOT_CONNECTED)
            status = context.getResources().getString(R.string.no_internet);
        return status;
    }
}
