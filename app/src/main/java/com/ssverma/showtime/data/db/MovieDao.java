package com.ssverma.showtime.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ssverma.showtime.model.Movie;

@Dao
public interface MovieDao {
    @Insert
    void insert(Movie movie);

    @Query("SELECT * FROM fav_movie_table")
    DataSource.Factory<Integer, Movie> getAllFavoriteMovies();

    @Query("DELETE FROM fav_movie_table WHERE id = :movieId")
    void deleteMovie(int movieId);

    @Query("SELECT id FROM fav_movie_table WHERE id = :movieId")
    LiveData<Boolean> isMovieFavorite(int movieId);
}
