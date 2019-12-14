package com.example.cuteboard;

public class RSSPost {

    private String Title;
    private String Content;
    private String Date;
    private String Image;
    private String Link;


    public RSSPost(String title, String content, String date, String image, String link)
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

    public String getLink() { return this.Link; }

    public void setLink(String link) { this.Link = link; }

}
