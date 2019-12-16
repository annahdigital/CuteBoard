package com.example.cuteboard.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cuteboard.Activities.MainActivity;
import com.example.cuteboard.R;

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkStateReader.getConnectivityStatusString(context);

        if (context.getClass().equals(MainActivity.class))
            ((MainActivity) context).networkStateChanged(status);

        if (status.equals(context.getResources().getString(R.string.no_internet))) {
            Toast toast = Toast.makeText(context,
                    status,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastContainer = (LinearLayout) toast.getView();
            ImageView catImageView = new ImageView(context);
            catImageView.setImageResource(R.drawable.kitty_wow);
            toastContainer.addView(catImageView, 0);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(context,
                    status,
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
