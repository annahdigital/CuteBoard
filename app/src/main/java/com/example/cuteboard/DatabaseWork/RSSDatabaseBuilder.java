package com.example.cuteboard.DatabaseWork;

import android.content.Context;

import androidx.room.Room;

public class RSSDatabaseBuilder {

    private static RSSDatabaseBuilder instance = null;
    private final RSSDatabase db;

    private RSSDatabaseBuilder(Context context)
    {
        // getting db instance
        db = Room.databaseBuilder(context, RSSDatabase.class, "rss_db").build();
    }

    public static RSSDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = new RSSDatabaseBuilder(context);
        return instance.db;
    }
}
