package com.example.cuteboard.DatabaseWork;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.cuteboard.Models.RSSPost;

@Database(entities = {RSSPost.class}, version = 1, exportSchema = false)
public abstract class RSSDatabase extends RoomDatabase{
    public abstract RSSPostDao getRSSPostDao();
}
