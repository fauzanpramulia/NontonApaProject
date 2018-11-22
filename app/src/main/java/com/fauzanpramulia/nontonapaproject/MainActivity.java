package com.fauzanpramulia.nontonapaproject;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.fauzanpramulia.nontonapaproject.adapter.MovieAdapter;
import com.fauzanpramulia.nontonapaproject.db.AppDatabase;
import com.fauzanpramulia.nontonapaproject.db.NowPlaying;
import com.fauzanpramulia.nontonapaproject.model.MovieItems;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClicked{

    //cara penggunaannya
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    MovieAdapter adapter;
    ArrayList<MovieItems> daftarFilm = new ArrayList<>();
    SharedPreferences preferences;
    int lastSeen;
    AppDatabase db;
    //http://api.themoviedb.org/3/movie/now_playing?api_key=a5914da7e79026cf3d11c42c298d7121
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getPreferences(Context.MODE_PRIVATE);
        ButterKnife.bind(this);

        //object database
        db = Room.databaseBuilder(this, AppDatabase.class, "tmdb.db")
                .allowMainThreadQueries()
                .build();
        adapter = new MovieAdapter();
        adapter.setHandler(this);

        int orientation= getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }



        recyclerView.setAdapter(adapter);

        lastSeen = preferences.getInt("last_seen_key", 1);
        if(lastSeen == 1){
            getNowPlayingMovie();
        }else if (lastSeen==2){
            getUpComingMovie();
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this,query,Toast.LENGTH_SHORT).show();
                getCariMovie(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                preferences = getPreferences(Context.MODE_PRIVATE);
                lastSeen = preferences.getInt("last_seen_key", 1);
                if(lastSeen == 1){
                    getNowPlayingMovie();
                }else if (lastSeen==2){
                    getUpComingMovie();
                }
                break;
            case R.id. menu_clear:
                adapter.setDataFilm(null);
                break;
            case R.id.menu_nowplaying:
                getNowPlayingMovie();
                break;
            case R.id.menu_upcoming:
                getUpComingMovie();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNowPlayingMovie(){
        progressBar.setVisibility(View.VISIBLE);


        if (isConnected()){
            //ambildata ke internet
            String API_BASE_URL = "https://api.themoviedb.org";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TmdbClient client =  retrofit.create(TmdbClient.class);

            Call<MovieList> call = client.getNowPlaying(BuildConfig.My_Db_api_key);
            call.enqueue(new Callback<MovieList>() {
                @Override
                public void onResponse(Call<MovieList> call, Response<MovieList> response)   {
                    //Disini kode kalau berhasil
                    // Toast.makeText(MainActivity.this, "berhasil", Toast.LENGTH_SHORT).show();
                    MovieList movieList = response.body();
                    List<MovieItems> listMovieItem = movieList.results;

                    saveMovieToDb(listMovieItem);

                    adapter.setDataFilm(new ArrayList<MovieItems>(listMovieItem));
                    progressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onFailure(Call<MovieList> call, Throwable t) {
                    //Disini kode kalau error
                    Toast.makeText(MainActivity.this, "Gagal Load Data", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            //ambil data ke db
            List<NowPlaying> nowPlayings = db.nowPlayingDao().getAllMovies();
            ArrayList<MovieItems> movies = new ArrayList<>();
            for (NowPlaying n : nowPlayings){
                MovieItems m = new MovieItems(
                        n.id,
                        n.title,
                        n.overview,
                        n.vote_average,
                        n.release_date,
                        n.poster_path
                );
                movies.add(m);
            }
            adapter.setDataFilm(movies);
            progressBar.setVisibility(View.INVISIBLE);
        }

        //15 november 2018
        getSupportActionBar().setTitle("Movie Now Playing");
        preferences = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences preferenceses = getSharedPreferences("datarahasia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("last_seen_key", 1);
        editor.apply();

    }
    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void saveMovieToDb(final List<MovieItems> listMovieItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(MovieItems m:listMovieItem){
                    NowPlaying nowPlaying = new NowPlaying();
                    nowPlaying.id = m.id;
                    nowPlaying.title = m.title;
                    nowPlaying.overview = m.overview;
                    nowPlaying.vote_average = m.vote_average;
                    nowPlaying.release_date = m.release_date;
                    nowPlaying.poster_path = m.poster_path;

                    db.nowPlayingDao().insertNowPlaying(nowPlaying);
                }
            }
        }).start();

    }

    private void getCariMovie(String name){
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String API_BASE_URL = "https://api.themoviedb.org";
        String BAHASA_URL = "en-US";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TmdbClient client =  retrofit.create(TmdbClient.class);

        Call<MovieList> call = client.getCariMovie(BuildConfig.My_Db_api_key, BAHASA_URL, name);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response)   {
                MovieList movieList = response.body();
                List<MovieItems> listMovieItem = movieList.results;
                adapter.setDataFilm(new ArrayList<MovieItems>(listMovieItem));
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void getUpComingMovie(){
        progressBar.setVisibility(View.VISIBLE);
        String API_BASE_URL = "https://api.themoviedb.org";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TmdbClient client =  retrofit.create(TmdbClient.class);

        Call<MovieList> call = client.getMoviesUpcoming(BuildConfig.My_Db_api_key);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response)   {
                //Disini kode kalau berhasil
                // Toast.makeText(MainActivity.this, "berhasil", Toast.LENGTH_SHORT).show();
                MovieList movieList = response.body();
                List<MovieItems> listMovieItem = movieList.results;
                adapter.setDataFilm(new ArrayList<MovieItems>(listMovieItem));
                progressBar.setVisibility(View.INVISIBLE);
                //15 november 2018
                getSupportActionBar().setTitle("Movie Up Coming");
                preferences = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences preferenceses = getSharedPreferences("datarahasia", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("last_seen_key", 2);
                editor.apply();
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                //Disini kode kalau error
                Toast.makeText(MainActivity.this, "Gagal Load Data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void ambilDataKeServer() {

        AmbilDataTask task = new AmbilDataTask();
        task.execute();
    }

    @Override
    public void clik(MovieItems m) {
        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra("movie", m);
        startActivity(i);
    }

    class AmbilDataTask extends AsyncTask<Void,Void, String>{
        String result;
        @Override
        protected String doInBackground(Void... voids) {
            //String result;
            String webUrl="http://api.themoviedb.org/3/movie/now_playing?api_key="+BuildConfig.My_Db_api_key;
            HttpURLConnection urlConnection;

            try{
                URL url = new URL(webUrl);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //ambil data ke server
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while((line= reader.readLine()) !=null){
                    buffer.append(line +"\n");
                }
                if (buffer.length()==0){
                    return null;
                }
                result = buffer.toString();
                //textView.setText(result);

            }catch (Exception e){

            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
           // textView.setText(result);
            super.onPostExecute(result);
        }
    }

}
