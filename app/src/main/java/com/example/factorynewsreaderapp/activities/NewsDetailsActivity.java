package com.example.factorynewsreaderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.factorynewsreaderapp.R;
import com.example.factorynewsreaderapp.adapters.ViewPagerAdapter;
import com.example.factorynewsreaderapp.fragments.ViewPagerItemFragment;
import com.example.factorynewsreaderapp.models.NewsItem;

import java.util.ArrayList;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


public class NewsDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    public static Handler mHandler;
    public static ViewPager viewPager;
    ArrayList<NewsItem> newsList;
    Integer newsPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        Bundle bundleObject = getIntent().getExtras();
        newsList = (ArrayList<NewsItem>) bundleObject.getSerializable("newsItems");
        newsPosition = bundleObject.getInt("position");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.view_pager);

        init();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                new AlertDialog.Builder(NewsDetailsActivity.this)
                        .setTitle("Greška")
                        .setMessage("Došlo je do pogreške prilikom učitavanja vijesti.")
                        .setCancelable(false)
                        .setPositiveButton("U REDU", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: Poruka uklonjena!");
                            }
                        })
                        .show();
            }
        };

    }

    private void init(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        for(NewsItem newsItem: newsList){
            ViewPagerItemFragment fragment = ViewPagerItemFragment.getInstance(newsItem);
            fragments.add(fragment);
        }
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(newsPosition);
    }
}
