package com.ssverma.showtime.ui;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;

import com.ssverma.showtime.R;
import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.data.Listing;
import com.ssverma.showtime.data.MoviesRepository;
import com.ssverma.showtime.data.NetworkState;
import com.ssverma.showtime.model.CastResponse;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.MovieDetailsResponse;
import com.ssverma.showtime.model.MovieKeyInfo;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.VideosResponse;
import com.ssverma.showtime.utils.AppUtility;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsViewModel extends AndroidViewModel {
    private final MoviesRepository repository;
    private final LiveData<NetworkState> initialLoadState;
    private final Application application;
    private final LiveData<Resource<VideosResponse>> videoLiveData;
    private final LiveData<PagedList<Review>> reviewsLiveData;
    private final LiveData<Boolean> isMovieFavorite;
    private final LiveData<Resource<MovieDetailsResponse>> movieDetails;
    private final LiveData<Resource<CastResponse>> casts;
    private MutableLiveData<Integer> movieIdLiveData = new MutableLiveData<>();

    public MovieDetailsViewModel(final Application application) {
        super(application);
        this.application = application;

        repository = new MoviesRepository(application);

        videoLiveData = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Resource<VideosResponse>>>() {
            @Override
            public LiveData<Resource<VideosResponse>> apply(Integer movieId) {
                return repository.getVideos(movieId);
            }
        });

        isMovieFavorite = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(Integer movieId) {
                return repository.isFavoriteMovie(movieId);
            }
        });

        movieDetails = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Resource<MovieDetailsResponse>>>() {
            @Override
            public LiveData<Resource<MovieDetailsResponse>> apply(Integer movieId) {
                return repository.getMovieDetails(movieId);
            }
        });

        final LiveData<Listing<Review>> reviewResult = Transformations.map(movieIdLiveData, new Function<Integer, Listing<Review>>() {
            @Override
            public Listing<Review> apply(Integer movieId) {
                return repository.getReviews(movieId);
            }
        });

        reviewsLiveData = Transformations.switchMap(reviewResult, new Function<Listing<Review>, LiveData<PagedList<Review>>>() {
            @Override
            public LiveData<PagedList<Review>> apply(Listing<Review> input) {
                return input.getPagedList();
            }
        });

        initialLoadState = Transformations.switchMap(reviewResult, new Function<Listing<Review>, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(Listing<Review> input) {
                return input.getInitialLoadState();
            }
        });

        casts = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Resource<CastResponse>>>() {
            @Override
            public LiveData<Resource<CastResponse>> apply(Integer movieId) {
                return repository.getMovieCasts(movieId);
            }
        });
    }

    public void updateMovieId(int movieId) {
        if (movieIdLiveData.getValue() != null && movieIdLiveData.getValue() == movieId) {
            return;
        }
        movieIdLiveData.setValue(movieId);
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

    public LiveData<Resource<MovieDetailsResponse>> getMovieDetails() {
        return movieDetails;
    }

    public LiveData<NetworkState> getInitialLoadState() {
        return initialLoadState;
    }

    public LiveData<Resource<CastResponse>> getCasts() {
        return casts;
    }

    public List<MovieKeyInfo> getMovieKeyInfo(MovieDetailsResponse movieDetails) {
        List<MovieKeyInfo> resultList = new ArrayList<>();

        String[] values = {
                movieDetails.isAdult() ? "Yes" : "No",
                AppUtility.addDollarSymbol(movieDetails.getBudget()),
                movieDetails.getLanguage(),
                movieDetails.getReleaseDate(),
                AppUtility.addDollarSymbol(movieDetails.getRevenue()),
                movieDetails.getRuntime() + " mins",
                movieDetails.getStatus(),
                movieDetails.getVoteAvg() + " / " + movieDetails.getVoteCount()
        };

        /*Tied up*/
        String[] labels = {
                application.getString(R.string.info_adult),
                application.getString(R.string.info_budget),
                application.getString(R.string.info_language),
                application.getString(R.string.info_release_date),
                application.getString(R.string.info_revenue),
                application.getString(R.string.info_runtime),
                application.getString(R.string.info_status),
                application.getString(R.string.info_vote)
        };

        /*Tied up*/
        int[] icons = {
                R.drawable.ic_adult,
                R.drawable.ic_budget,
                R.drawable.ic_language,
                R.drawable.ic_release_date,
                R.drawable.ic_revenue,
                R.drawable.ic_runtime,
                R.drawable.ic_status,
                R.drawable.ic_vote,
        };

        for (int i = 0; i < values.length; i++) {
            MovieKeyInfo keyInfo = new MovieKeyInfo();
            keyInfo.setLabel(labels[i]);
            keyInfo.setValue(values[i]);
            keyInfo.setIcon(icons[i]);
            resultList.add(keyInfo);
        }

        return resultList;
    }

}
