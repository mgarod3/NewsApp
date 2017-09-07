package com.example.mercedes.newsapp;

public class Article {

    //Article title
    private String mTitle;

    // Author
    private String mContributor;

    // Section
    private String mSection;

    //Publication date
    private String mDate;

    /**
     * Website URL of the article
     */
    private String mUrl;

    //constructor
    public Article(String title, String contributor, String section, String date, String url) {
        this.mTitle = title;
        this.mContributor = contributor;
        this.mSection = section;
        this.mDate = date;
        this.mUrl = url;
    }

    /**
     * Get methods
     */
    public String getTitle() {
        return mTitle;
    }

    public String getContributor() {
        return mContributor;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}


