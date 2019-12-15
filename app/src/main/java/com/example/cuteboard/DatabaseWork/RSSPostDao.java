package com.example.cuteboard.DatabaseWork;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cuteboard.Models.RSSPost;

import java.util.List;

@Dao
public interface RSSPostDao {

    @Insert
    void insertAll(List<RSSPost> rssPosts);

    @Insert
    void insert(RSSPost rssPost);

    @Query("DELETE FROM rsspost")
    void deleteAll();

    @Query("SELECT * FROM rsspost")
    List<RSSPost> getAll();
}
