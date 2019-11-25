package com.example.factorynewsreaderapp.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.factorynewsreaderapp.models.NewsItem;
import com.example.factorynewsreaderapp.repositories.ArticleRepository;

import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel implements ArticleRepository.AsyncResponseArticles{
    private static final String TAG = "MyActivity";
    private MutableLiveData<ArrayList<NewsItem>> mNewsItems;

    public void init(Context context){
        ArticleRepository mRepo = ArticleRepository.getInstance();
        mRepo.setContext(context, this);
        mNewsItems = mRepo.getNewsItems();
        Log.d(TAG, "init: " + mNewsItems.getValue());
    }

    public LiveData<ArrayList<NewsItem>> getNewsItems(){
        return mNewsItems;
    }

    @Override
    public void processFinish(ArrayList<NewsItem> output) {
        mNewsItems.setValue(output);
        Log.d(TAG, "processFinish: " + mNewsItems.getValue());
    }
}
