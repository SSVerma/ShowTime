package com.ssverma.showtime.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ssverma.showtime.model.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DB_NAME = "movie_database";
    private static MovieDatabase INSTANCE;

    public static MovieDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract MovieDao getMovieDao();

}
