package com.scarwe.freechess.api;

import com.scarwe.freechess.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    // query for news api
    @GET("everything")
    Call<News> getNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}
