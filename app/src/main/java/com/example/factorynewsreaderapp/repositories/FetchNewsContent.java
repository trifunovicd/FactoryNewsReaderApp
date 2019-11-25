package com.example.factorynewsreaderapp.repositories;

import android.os.AsyncTask;
import android.os.Message;

import com.example.factorynewsreaderapp.activities.NewsDetailsActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class FetchNewsContent extends AsyncTask<Void, Void, String> {

    private String url, data;
    public AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public FetchNewsContent(String url, AsyncResponse delegate) {
        this.url = url;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            Document doc = Jsoup.connect(url).get();
            Elements content = doc.select("div.story-body__inner h2, div.story-body__inner p, div.story-body__inner ul, div.vxp-media__body .vxp-media__headline, div.vxp-media__body .vxp-media__summary");
            data = content.toString();

        } catch (IOException e) {
            e.printStackTrace();
            Message message = NewsDetailsActivity.mHandler.obtainMessage();
            message.sendToTarget();
            data = "<p>Došlo je do pogreške prilikom učitavanja vijesti.</p><p>Provjerite internetsku vezu.</p>";
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
