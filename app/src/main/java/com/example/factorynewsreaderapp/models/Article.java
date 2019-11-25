package com.example.factorynewsreaderapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Article {

    @SerializedName("articles")
    @Expose
    private ArrayList<NewsItem> articles;

    public Article(ArrayList<NewsItem> articles) {
        this.articles = articles;
    }

    public ArrayList<NewsItem> getArticles() {
        return articles;
    }
}
