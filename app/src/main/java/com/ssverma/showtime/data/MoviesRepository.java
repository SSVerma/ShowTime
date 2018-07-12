package com.ssverma.showtime.data;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.api.TmdbService;
import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.data.db.MovieDao;
import com.ssverma.showtime.data.db.MovieDatabase;
import com.ssverma.showtime.model.CastResponse;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.MovieDetailsResponse;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.VideosResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {
    private static final int PAGE_SIZE = 10;
    private final TmdbService tmdbService;
    private MovieDao movieDao;

    public MoviesRepository(Application application) {
        MovieDatabase database = MovieDatabase.getDatabase(application);
        movieDao = database.getMovieDao();
        tmdbService = ApiUtils.getTmdbService();
    }

    public Listing<Movie> getMovies(String path) {

        MovieDataSourceFactory movieDataSourceFactory = new MovieDataSourceFactory(path);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .build();

        LiveData<PagedList<Movie>> movies = new LivePagedListBuilder<>(movieDataSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

        return new Listing<>(
                movies,
                Transformations.switchMap(movieDataSourceFactory.getDataSource(), new Function<MovieDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(MovieDataSource input) {
                        return input.getNetworkState();
                    }
                }),
                Transformations.switchMap(movieDataSourceFactory.getDataSource(), new Function<MovieDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(MovieDataSource input) {
                        return input.getInitialLoadingStates();
                    }
                })
        );
    }

    public Listing<Movie> getFavoriteMovies() {
        DataSource.Factory<Integer, Movie> dbSourceFactory = movieDao.getAllFavoriteMovies();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .build();

        LiveData<PagedList<Movie>> movies = new LivePagedListBuilder<>(dbSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

        MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
        initialLoad.setValue(NetworkState.LOADED);

        return new Listing<>(
                movies,
                null,
                initialLoad);
    }

    public Listing<Review> getReviews(Integer movieId) {

        ReviewDataSourceFactory reviewDataSourceFactory = new ReviewDataSourceFactory(movieId);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        LiveData<PagedList<Review>> reviews = new LivePagedListBuilder<Integer, Review>(reviewDataSourceFactory, config)
                .setInitialLoadKey(1)
                .build();

        return new Listing<>(
                reviews,
                null,
                Transformations.switchMap(reviewDataSourceFactory.getDataSourceLiveData(), new Function<ReviewDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(ReviewDataSource input) {
                        return input.getInitialState();
                    }
                })
        );
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

    public LiveData<Resource<MovieDetailsResponse>> getMovieDetails(int movieId) {
        final MutableLiveData<Resource<MovieDetailsResponse>> data = new MutableLiveData<>();

        tmdbService.getMovieDetails(movieId).enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetailsResponse> call, @NonNull Response<MovieDetailsResponse> response) {
                data.setValue(new Resource<MovieDetailsResponse>(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailsResponse> call, @NonNull Throwable t) {
                data.setValue(new Resource<MovieDetailsResponse>(t));
            }
        });

        return data;
    }

    public LiveData<Resource<CastResponse>> getMovieCasts(int movieId) {
        final MutableLiveData<Resource<CastResponse>> data = new MutableLiveData<>();

        tmdbService.getMovieCasts(movieId).enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(@NonNull Call<CastResponse> call, @NonNull Response<CastResponse> response) {
                data.setValue(new Resource<CastResponse>(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<CastResponse> call, @NonNull Throwable t) {
                data.setValue(new Resource<CastResponse>(t));
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
