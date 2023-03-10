package com.scarwe.freechess.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.scarwe.freechess.R;
import com.scarwe.freechess.api.ApiClient;
import com.scarwe.freechess.api.ApiInterface;
import com.scarwe.freechess.draw.Adapter;
import com.scarwe.freechess.draw.Utils;
import com.scarwe.freechess.models.Article;
import com.scarwe.freechess.models.News;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    public static final String API_KEY = "76296d75652b456584636a1d755230ee";
    private final int bgColor = Color.parseColor("#393939");
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Article> articles = new ArrayList<>();
    private Adapter adapter;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setActivityBgColor();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(NewsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        loadJSON();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Chess News");

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#393939"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    public void loadJSON() {
        ApiInterface apiInterface = ApiClient.getAPIClient().create(ApiInterface.class);

        Call<News> call;
        String query = "chess";
        call = apiInterface.getNews(query, API_KEY);

        call.enqueue(new Callback<News>() {

            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    articles = (ArrayList<Article>) response.body().getArticles();
                    ArrayList<Article> filteredArticles = new ArrayList<>();
                    for (int i = 0; i < articles.size(); i++) {
                        if (articles.get(i).getTitle().toLowerCase().contains("chess")) {
                            filteredArticles.add(articles.get(i));
                        }
                    }
                    adapter = new Adapter(NewsActivity.this, filteredArticles);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(NewsActivity.this, "No Result!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
            }
        });
    }

    private void setActivityBgColor() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(bgColor);
    }
}