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
import com.ssverma.showtime.data.SharedPrefHelper;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.MovieDetailsResponse;
import com.ssverma.showtime.model.MovieKeyInfo;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.SortOptions;
import com.ssverma.showtime.model.VideosResponse;
import com.ssverma.showtime.utils.AppUtility;

import java.util.ArrayList;
import java.util.List;

public class MoviesViewModel extends AndroidViewModel {
    private final MoviesRepository repository;
    private final LiveData<NetworkState> networkState;
    private final LiveData<NetworkState> initialLoadState;
    private final Application application;
    private final LiveData<Resource<VideosResponse>> videoLiveData;
    private final LiveData<PagedList<Review>> reviewsLiveData;
    private final LiveData<PagedList<Movie>> movies;
    private final LiveData<Boolean> isMovieFavorite;
    private final LiveData<Resource<MovieDetailsResponse>> movieDetails;
    private MutableLiveData<Integer> movieIdLiveData = new MutableLiveData<>();
    private MutableLiveData<String> pathLiveData = new MutableLiveData<>();

    public MoviesViewModel(final Application application) {
        super(application);
        this.application = application;

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

        final LiveData<Listing<Movie>> moviesResult = Transformations.map(pathLiveData, new Function<String, Listing<Movie>>() {
            @Override
            public Listing<Movie> apply(String path) {
                if (path.equals(application.getString(R.string.favorite_path))) {
                    return repository.getFavoriteMovies();
                }
                return repository.getMovies(path);
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

        movieDetails = Transformations.switchMap(movieIdLiveData, new Function<Integer, LiveData<Resource<MovieDetailsResponse>>>() {
            @Override
            public LiveData<Resource<MovieDetailsResponse>> apply(Integer movieId) {
                return repository.getMovieDetails(movieId);
            }
        });

    }

    public void updateMovieId(int movieId) {
        movieIdLiveData.setValue(movieId);
    }

    public void updatePath(String path) {
        this.pathLiveData.setValue(path);
        SharedPrefHelper.saveSortSelectedPath(application, path);
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

    public LiveData<Resource<MovieDetailsResponse>> getMovieDetails() {
        return movieDetails;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoadState() {
        return initialLoadState;
    }

    public List<SortOptions> getSortOptions() {
        String[] sortOptions = {
                application.getString(R.string.most_popular_label),
                application.getString(R.string.top_rated_label),
                application.getString(R.string.favorite_label)
        };

        String[] sortOptionsPath = {
                application.getString(R.string.popular_path),
                application.getString(R.string.top_rated_path),
                application.getString(R.string.favorite_path)
        };

        List<SortOptions> resultList = new ArrayList<>();

        for (int i = 0; i < sortOptions.length; i++) {
            SortOptions sortOption = new SortOptions();
            sortOption.setSortOptionLabel(sortOptions[i]);
            sortOption.setSortOptionPath(sortOptionsPath[i]);
            resultList.add(sortOption);
        }

        return resultList;
    }

    public String getLastSelectedSortPath() {
        String lastSortPath = SharedPrefHelper.getLastSortSelectedPath(application);
        return lastSortPath == null ? application.getString(R.string.popular_path) : lastSortPath;
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
                movieDetails.getVoteAvg() + "/" + movieDetails.getVoteCount()
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
