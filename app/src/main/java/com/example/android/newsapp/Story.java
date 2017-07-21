package com.example.android.newsapp;

/**
 * Created by Maks on 21/07/2017.
 */

// An object that contains information about a news story
public class Story {

    private String mTitle;
    private String mSection;
    private String mUrl;

    // Public constructor with 2 inputs - title & topic
    public Story(String title, String section, String url){
        mTitle = title;
        mSection = section;
        mUrl = url;
    }

    // Getter methods for Story class
    public String getTitle (){return mTitle;}
    public String getSection (){return mSection;}
    public String getUrl (){return mUrl;}

}
