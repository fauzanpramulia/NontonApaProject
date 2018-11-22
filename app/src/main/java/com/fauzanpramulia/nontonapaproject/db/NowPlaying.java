package com.fauzanpramulia.nontonapaproject.db;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "now_playing")
public class NowPlaying {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "overview")
    public String overview;

    @ColumnInfo(name = "vote_average")
    public double vote_average;

    @ColumnInfo(name = "release_date")
    public String release_date;

    @ColumnInfo(name = "poster_path")
    public String poster_path;

}
