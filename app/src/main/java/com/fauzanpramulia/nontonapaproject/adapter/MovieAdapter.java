package com.fauzanpramulia.nontonapaproject.adapter;

import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fauzanpramulia.nontonapaproject.R;
import com.fauzanpramulia.nontonapaproject.model.MovieItems;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    ArrayList<MovieItems> dataFilm;
    OnItemClicked Handler;
    public void setDataFilm(ArrayList<MovieItems> films){
        this.dataFilm = films;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemClicked clickHandler) {
        this.Handler = clickHandler;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        MovieHolder holder = new MovieHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        MovieItems movie = dataFilm.get(position);
        holder.textTitle.setText(movie.getTitle());
        holder.textRating.setText(String.valueOf(movie.getVote_average()));
        holder.textTanggalRilis.setText(movie.getRelease_date());
        String url = "http://image.tmdb.org/t/p/w300" + movie.getPoster_path();
        Glide.with(holder.itemView)
                .load(url)
                .into(holder.imagePoster);

    }

    @Override
    public int getItemCount() {
        if (dataFilm !=null){
           return dataFilm.size();
        }
        return 0;
    }

    public class MovieHolder extends RecyclerView.ViewHolder {
        ImageView imagePoster;
        TextView textTitle;
        TextView textRating;
        TextView textTanggalRilis;
        public MovieHolder(View itemView) {
            super(itemView);
            imagePoster = itemView.findViewById(R.id.img_poster);
            textTitle = itemView.findViewById(R.id.textTitle);
            textRating = itemView.findViewById(R.id.textRating);
            textTanggalRilis = itemView.findViewById(R.id.textTanggalRilis);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Handler.clik(dataFilm.get(getAdapterPosition()));
                }
            });
        }
    }
    public interface OnItemClicked{
        void clik(MovieItems m);
    }
}
