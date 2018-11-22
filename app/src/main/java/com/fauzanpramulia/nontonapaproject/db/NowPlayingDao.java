package com.fauzanpramulia.nontonapaproject.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;



@Dao
public interface NowPlayingDao{

    @Query("SELECT * FROM now_playing")
    List<NowPlaying> getAllMovies();

    @Query("SELECT * FROM now_playing WHERE id = :id")
    NowPlaying getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNowPlaying(NowPlaying nowPlaying);

    @Query("DELETE FROM now_playing")
    void clear();
    @Delete
    void delete(NowPlaying nowPlaying);
}
