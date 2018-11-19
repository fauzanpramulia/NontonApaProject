package com.fauzanpramulia.nontonapaproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fauzanpramulia.nontonapaproject.model.MovieItems;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_title)TextView detailTitle;
    @BindView(R.id.detail_overview)TextView detailDesk;
    @BindView(R.id.detail_rating)TextView detailRating;
    @BindView(R.id.detail_TanggalRilis)TextView detailTanggal;
    @BindView(R.id.detail_poster) ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent i = getIntent();
       // if (i !=null){
            MovieItems movie = getIntent().getParcelableExtra("movie");

            detailTitle.setText(movie.getTitle());
            detailDesk.setText(movie.getOverview());
            detailRating.setText(String.valueOf(movie.getVote_average()));
            detailTanggal.setText(movie.getRelease_date());

            String url = "http://image.tmdb.org/t/p/w300" + movie.getPoster_path();
            Glide.with(this)
                    .load(url)
                    .into(imgView);
       // }
    }
}
