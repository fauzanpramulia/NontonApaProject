package com.fauzanpramulia.nontonapaproject.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {NowPlaying.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{

    public abstract NowPlayingDao nowPlayingDao();

}
