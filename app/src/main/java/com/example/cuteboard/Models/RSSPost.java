package com.example.cuteboard.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RSSPost {

    private String Title;
    private String Content;
    private String Date;
    private String Image;
    private String CachedImage;
    @NonNull
    @PrimaryKey
    private String Link;


    public RSSPost(String title, String content, String date, String image, @NonNull String link)
    {
        this.Title = title;
        this.Content = content;
        this.Date = date;
        this.Link = link;
        this.Image = image;
    }

    public RSSPost() {}


    public String getTitle() { return this.Title; }

    public void setTitle(String title) { this.Title = title; }

    public String getContent() { return this.Content; }

    public void setContent(String content) { this.Content = content; }

    public String getDate() { return this.Date; }

    public void setDate(String date) { this.Date = date; }

    public String getImage() { return this.Image; }

    public void setImage(String image) { this.Image = image; }

    public void setCachedImage(String image) { this.CachedImage = image; }

    public String getCachedImage() { return this.CachedImage; }

    @NonNull
    public String getLink() { return this.Link; }

    public void setLink(@NonNull String link) { this.Link = link; }

}
