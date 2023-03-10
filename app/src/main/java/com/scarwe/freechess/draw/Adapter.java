package com.scarwe.freechess.draw;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.scarwe.freechess.R;
import com.scarwe.freechess.activity.NewsActivity;
import com.scarwe.freechess.models.Article;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    // For debugging
    private static String TAG = "ArticleAdapter";

    // List to hold news articles
    private ArrayList<Article> articles;
    private Context contx;

    public Adapter(Context context, ArrayList<Article> list) {
        this.articles = list;
        this.contx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int type) {
        View view = LayoutInflater.from(contx).inflate(R.layout.item, vg, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
        Article currArt = articles.get(pos);

        // Set the current article's title to this view
        vh.title.setText(currArt.getTitle());
        vh.description.setText(currArt.getDescription());
        vh.author.setText(currArt.getAuthor());
        vh.source.setText(currArt.getSource().getName());
        vh.publishedAt.setText(" \u2022 " + Utils.DateToTimeFormat(currArt.getPublishedAt()));;
        vh.time.setText(Utils.DateFormat(currArt.getPublishedAt()));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawableColor());
        requestOptions.error(Utils.getRandomDrawableColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        // Gets the author of the article and the shortened date it was released on
        String imageUrl = currArt.getUrlToImage();

        // Use Picasso dependency to load the image needed
        Glide.with(contx).load(imageUrl).apply(requestOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                vh.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                vh.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).transition(DrawableTransitionOptions.withCrossFade()).into(vh.image);

        vh.image.setContentDescription(currArt.getDescription());

        vh.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currArt.getUrl()));
            contx.startActivity(browserIntent);
        });
    }

    /*
     * Public access point used to get the number of articles in the list
     * @returns articles.size() - Number of Article objects in the list
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, author, source, time, publishedAt;
        private ProgressBar progressBar;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.desc);
            image = itemView.findViewById(R.id.img);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.time);
            publishedAt = itemView.findViewById(R.id.publishedAt);
            source = itemView.findViewById(R.id.source);
            progressBar = itemView.findViewById(R.id.progress_load_photo);
        }

    }
}