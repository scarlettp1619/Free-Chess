package com.scarwe.freechess.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.scarwe.freechess.R;
import com.scarwe.freechess.api.ApiClient;
import com.scarwe.freechess.api.ApiInterface;
import com.scarwe.freechess.draw.Adapter;
import com.scarwe.freechess.draw.Utils;
import com.scarwe.freechess.models.Article;
import com.scarwe.freechess.models.News;

import java.io.Console;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    // api key for news api
    public static final String API_KEY = "76296d75652b456584636a1d755230ee";
    private final int bgColor = Color.parseColor("#393939");
    // recyclerview for showing news items
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Article> articles = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        setActivityBgColor();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(NewsActivity.this);
        // settings for recycler view
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        loadJSON();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Chess News");

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#393939"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        Button exitButton = findViewById(R.id.exit_button);

        exitButton.setOnClickListener(v -> finish());
    }

    // loading news (returns json)
    public void loadJSON() {
        ApiInterface apiInterface = ApiClient.getAPIClient().create(ApiInterface.class);

        Call<News> call;
        // ensures news is chess related
        String query = "chess";
        call = apiInterface.getNews(query, API_KEY);

        call.enqueue(new Callback<News>() {

            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                // if response is fine
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    articles = (ArrayList<Article>) response.body().getArticles();
                    ArrayList<Article> filteredArticles = new ArrayList<>();
                    for (int i = 0; i < articles.size(); i++) {
                        // filters out non chess articles (a few show up)
                        if (articles.get(i).getTitle().toLowerCase().contains("chess")) {
                            filteredArticles.add(articles.get(i));
                        }
                    }
                    // shows articles on page
                    adapter = new Adapter(NewsActivity.this, filteredArticles);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(NewsActivity.this, "No Results!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(NewsActivity.this, "Error searching news!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setActivityBgColor() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(bgColor);
    }

}