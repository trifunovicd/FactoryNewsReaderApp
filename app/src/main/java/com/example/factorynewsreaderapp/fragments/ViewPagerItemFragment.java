package com.example.factorynewsreaderapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.factorynewsreaderapp.R;
import com.example.factorynewsreaderapp.models.NewsItem;
import com.example.factorynewsreaderapp.repositories.FetchNewsContent;

public class ViewPagerItemFragment extends Fragment implements FetchNewsContent.AsyncResponse {

    private ImageView newsImage;
    private TextView newsTitle, newsContent;
    private NewsItem newsItem;

    public static ViewPagerItemFragment getInstance(NewsItem newsItem){
        ViewPagerItemFragment fragment = new ViewPagerItemFragment();
        if(newsItem != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("newsItem", newsItem);
            fragment.setArguments(bundle);
        }
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null){
            newsItem = getArguments().getParcelable("newsItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_pager,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        newsImage =  view.findViewById(R.id.newsImage);
        newsTitle = view.findViewById(R.id.newsTitle);
        newsContent = view.findViewById(R.id.textViewNews);

        init();
    }

    private void init(){
        if(newsItem != null){
            Glide.with(getActivity()).load(newsItem.getImageUrl()).centerCrop().into(newsImage);
            newsTitle.setText(newsItem.getTitle());
            newsContent.setText(getActivity().getString(R.string.loading));
            new FetchNewsContent(newsItem.getNewsUrl(),this).execute();
        }
    }

    @Override
    public void processFinish(String output) {
        newsContent.setText(Html.fromHtml(output));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.open_in_browser_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.open_in_browser){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(newsItem.getNewsUrl()));
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
