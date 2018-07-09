package com.ssverma.showtime.ui;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;

import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.data.Listing;
import com.ssverma.showtime.data.MoviesRepository;
import com.ssverma.showtime.data.NetworkState;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.VideosResponse;

public class MoviesViewModel extends AndroidViewModel {
    private final MoviesRepository repository;
    private final LiveData<NetworkState> networkState;
    private final LiveData<NetworkState> initialLoadState;
    private LiveData<Resource<VideosResponse>> videoLiveData;
    private LiveData<PagedList<Review>> reviewsLiveData;
    private LiveData<PagedList<Movie>> movies;
    private MutableLiveData<Integer> movieIdLiveData = new MutableLiveData<>();
    private MutableLiveData<String> filterLiveData = new MutableLiveData<>();
    private LiveData<Boolean> isMovieFavorite;

    public MoviesViewModel(Application application) {
        super(application);
        repository = new MoviesRepository(application);

        videoLiveData = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Resource<VideosResponse>>>() {
            @Override
            public LiveData<Resource<VideosResponse>> apply(Integer movieId) {
                return repository.getVideos(movieId);
            }
        });

        reviewsLiveData = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<PagedList<Review>>>() {
            @Override
            public LiveData<PagedList<Review>> apply(Integer movieId) {
                return repository.getReviews(movieId);
            }
        });

        isMovieFavorite = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(Integer movieId) {
                return repository.isFavoriteMovie(movieId);
            }
        });

        final LiveData<Listing<Movie>> moviesResult = Transformations.map(filterLiveData, new Function<String, Listing<Movie>>() {
            @Override
            public Listing<Movie> apply(String input) {
                return repository.getMovies(input);
            }
        });

        movies = Transformations.switchMap(moviesResult, new Function<Listing<Movie>, LiveData<PagedList<Movie>>>() {
            @Override
            public LiveData<PagedList<Movie>> apply(Listing<Movie> input) {
                return input.getPagedList();
            }
        });

        networkState = Transformations.switchMap(moviesResult, new Function<Listing<Movie>, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(Listing<Movie> input) {
                return input.getNetworkState();
            }
        });

        initialLoadState = Transformations.switchMap(moviesResult, new Function<Listing<Movie>, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(Listing<Movie> input) {
                return input.getInitialLoadState();
            }
        });
    }

    public void updateMovieId(int movieId) {
        movieIdLiveData.setValue(movieId);
    }

    public void updateFilter(String filter) {
        this.filterLiveData.setValue(filter);
    }

    public LiveData<Resource<VideosResponse>> getVideos() {
        return videoLiveData;
    }

    public LiveData<PagedList<Review>> getReviews() {
        return reviewsLiveData;
    }

    public LiveData<Boolean> isMovieFavorite() {
        return isMovieFavorite;
    }

    public void addToFavorite(Movie movie) {
        repository.insertMovie(movie);
    }

    public void removeFromFavorite(int movieId) {
        repository.deleteMovie(movieId);
    }

    public LiveData<PagedList<Movie>> getMovies() {
        return movies;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoadState() {
        return initialLoadState;
    }
}
