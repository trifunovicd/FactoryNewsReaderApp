package com.example.factorynewsreaderapp.repositories;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.factorynewsreaderapp.models.Article;
import com.example.factorynewsreaderapp.models.NewsItem;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class ArticleRepository {
    private static final String TAG = "MyActivity";
    private static ArticleRepository instance;
    private ArrayList<NewsItem> newsItems = new ArrayList<>();
    private Context mContext;
    public AsyncResponseArticles delegate = null;
    private Integer counter = 0;

    public interface AsyncResponseArticles {
        void processFinish(ArrayList<NewsItem> output);
    }

    public static ArticleRepository getInstance(){
        if(instance == null){
            instance = new ArticleRepository();
        }
        return instance;
    }

    public void setContext(Context context, AsyncResponseArticles delegate){
        this.mContext = context;
        this.delegate = delegate;
    }

    public MutableLiveData<ArrayList<NewsItem>> getNewsItems(){
        setNewsItems();

        MutableLiveData<ArrayList<NewsItem>> data = new MutableLiveData<>();
        data.setValue(newsItems);
        Log.d(TAG, "getNewsItems: data " + data.getValue());
        return data;
    }

    private void setNewsItems() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        SharedPreferences pref_json = mContext.getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        final String old_json = pref_json.getString("jsonData", "");

        SharedPreferences pref_date = mContext.getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Date lastStoredDate = new Date(pref_date.getLong("lastTime", 0));

        Date currentDate = new Date(System.currentTimeMillis());
        long difference = currentDate.getTime() - lastStoredDate.getTime();
        long seconds = difference / 1000;
        long minutes = seconds / 60;

        if (minutes > 5 || old_json.length() == 0) {
            counter++;
            Log.d(TAG, "parseJSON: Prošlo je više od 5 min!");

            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Učitavanje vijesti...");
            progressDialog.show();

            String url = "https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=6946d0c07a1c4555a4186bfcade76398";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();

                    Log.d(TAG, "onResponse: Vijesti osvježene!");

                    mContext.getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit().putString("jsonData", response.toString()).apply();

                    SharedPreferences pref = mContext.getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                    String new_json = pref.getString("jsonData", "");

                    Date date = new Date(System.currentTimeMillis());
                    mContext.getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit().putLong("lastTime", date.getTime()).apply();

                    FillNewsList(new_json);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressDialog.dismiss();

                    Log.d(TAG, "onErrorResponse: Response error!");

                    new AlertDialog.Builder(mContext)
                            .setTitle("Greška")
                            .setMessage("Došlo je do pogreške prilikom učitavanja vijesti.")
                            .setCancelable(false)
                            .setPositiveButton("U REDU", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "onClick: Poruka uklonjena!");

                                    if (old_json.length() != 0) {
                                        Log.d(TAG, "onClick: Prikazujem stare vijesti!");
                                        FillNewsList(old_json);
                                    }
                                }
                            })
                            .show();
                }
            });

            requestQueue.add(request);
        } else {
            FillNewsList(old_json);
        }
    }

    private void FillNewsList(String json){
        Log.d(TAG, "FillNewsList: Punim listu sa vijestima!");

        Gson gson = new Gson();
        Article articles = gson.fromJson(json, Article.class);
        newsItems = articles.getArticles();
        Log.d(TAG, "FillNewsList: " + newsItems);
        if(counter != 0){
            delegate.processFinish(newsItems);
            counter = 0;
        }
    }
}
