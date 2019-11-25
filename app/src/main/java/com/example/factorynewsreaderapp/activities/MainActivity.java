package com.example.factorynewsreaderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.factorynewsreaderapp.R;
import com.example.factorynewsreaderapp.adapters.RecyclerViewAdapter;
import com.example.factorynewsreaderapp.models.NewsItem;
import com.example.factorynewsreaderapp.viewmodels.MainActivityViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "MyActivity";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private ArrayList<NewsItem> newsItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mainActivityViewModel.init(MainActivity.this);

        mainActivityViewModel.getNewsItems().observe(this, new Observer<ArrayList<NewsItem>>() {
            @Override
            public void onChanged(ArrayList<NewsItem> newsItems) {
                Log.d(TAG, "onChanged: " + newsItems);
                recyclerViewAdapter.updateNewsItems(newsItems);
                newsItemsList = newsItems;
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnItemClickListener(MainActivity.this);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: " + newsItemsList);

        Intent detailsIntent = new Intent(this, NewsDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("newsItems", newsItemsList);
        bundle.putInt("position", position);
        detailsIntent.putExtras(bundle);

        startActivity(detailsIntent);
    }
}
