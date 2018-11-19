package com.fauzanpramulia.nontonapaproject.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "now_playing")
public class NowPlaying {
    @PrimaryKey
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
