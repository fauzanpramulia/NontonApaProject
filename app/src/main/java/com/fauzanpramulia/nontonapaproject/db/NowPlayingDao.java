package com.fauzanpramulia.nontonapaproject.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface NowPlayingDao{

    @Query("SELECT * FROM now_playing")
    List<NowPlaying> getAll();

    @Query("SELECT * FROM now_playing WHERE id = :id")
    NowPlaying getById(int id);

    @Insert
    void insertAll(NowPlaying... nowPlayings);

    @Delete
    void delete(NowPlaying nowPlaying);
}
