package com.ssverma.showtime.data;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.api.TmdbService;
import com.ssverma.showtime.common.LoadingState;
import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.data.db.MovieDao;
import com.ssverma.showtime.data.db.MovieDatabase;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.VideosResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {
    private static final int PAGE_SIZE = 10;
    private final TmdbService tmdbService;
    private ReviewDataSourceFactory reviewDataSourceFactory;
    private MovieDataSourceFactory movieDataSourceFactory;
    private LiveData<PagedList<Review>> reviews;
    private LiveData<PagedList<Movie>> movies;
    private MovieDao movieDao;

    public MoviesRepository(Application application) {
        MovieDatabase database = MovieDatabase.getDatabase(application);
        movieDao = database.getMovieDao();
        tmdbService = ApiUtils.getTmdbService();
    }

    public LiveData<PagedList<Movie>> getMovies(String filter) {
        if (movies != null) {
            return movies;
        }

        movieDataSourceFactory = new MovieDataSourceFactory(filter);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .build();

        movies = new LivePagedListBuilder<>(movieDataSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

        return movies;
    }

    public LiveData<LoadingState> getMoviesLoadingStates() {
        return Transformations.switchMap(movieDataSourceFactory.getDataSource(), new Function<MovieDataSource, LiveData<LoadingState>>() {
            @Override
            public LiveData<LoadingState> apply(MovieDataSource movieDataSource) {
                return movieDataSource.getLoadingStates();
            }
        });
    }

    public LiveData<LoadingState> getMoviesInitialLoadingState() {
        return Transformations.switchMap(movieDataSourceFactory.getDataSource(), new Function<MovieDataSource, LiveData<LoadingState>>() {
            @Override
            public LiveData<LoadingState> apply(MovieDataSource movieDataSource) {
                return movieDataSource.getInitialLoadingStates();
            }
        });
    }

    public LiveData<PagedList<Review>> getReviews(Integer movieId) {
        if (reviews != null) {
            return reviews;
        }

        reviewDataSourceFactory = new ReviewDataSourceFactory(movieId);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        reviews = new LivePagedListBuilder<Integer, Review>(reviewDataSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

        return reviews;
    }

    public LiveData<LoadingState> getReviewsLoadingState() {
        return Transformations.switchMap(reviewDataSourceFactory.getDataSourceLiveData(), new Function<ReviewDataSource, LiveData<LoadingState>>() {
            @Override
            public LiveData<LoadingState> apply(ReviewDataSource reviewDataSource) {
                return reviewDataSource.getLoadingStates();
            }
        });
    }

    public LiveData<Resource<VideosResponse>> getVideos(int movieId) {
        final MutableLiveData<Resource<VideosResponse>> data = new MutableLiveData<>();

        tmdbService.getVideos(movieId).enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {
                data.setValue(new Resource<VideosResponse>(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {
                data.setValue(new Resource<VideosResponse>(t));
            }
        });

        return data;
    }

    public LiveData<Boolean> isFavoriteMovie(int movieId) {
        return movieDao.isMovieFavorite(movieId);
    }

    public void insertMovie(Movie movie) {
        new MovieInsertTask(movieDao).execute(movie);
    }

    public void deleteMovie(int movieId) {
        new MovieDeleteTask(movieDao).execute(movieId);
    }

    private static class MovieInsertTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao movieDao;

        MovieInsertTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.insert(movies[0]);
            return null;
        }
    }

    private static class MovieDeleteTask extends AsyncTask<Integer, Void, Void> {
        private MovieDao movieDao;

        MovieDeleteTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Integer... movieIds) {
            movieDao.deleteMovie(movieIds[0]);
            return null;
        }
    }

}
