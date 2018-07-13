package com.ssverma.showtime.ui.listing;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.ssverma.showtime.R;
import com.ssverma.showtime.data.Listing;
import com.ssverma.showtime.data.MoviesRepository;
import com.ssverma.showtime.data.NetworkState;
import com.ssverma.showtime.data.SharedPrefHelper;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.SortOptions;

import java.util.ArrayList;
import java.util.List;

public class MoviesListingViewModel extends AndroidViewModel {
    private final Application application;
    private final MoviesRepository repository;
    private final LiveData<NetworkState> networkState;
    private final LiveData<NetworkState> initialLoadState;
    private final LiveData<PagedList<Movie>> movies;
    private MutableLiveData<String> pathLiveData = new MutableLiveData<>();

    public MoviesListingViewModel(@NonNull final Application application) {
        super(application);
        this.application = application;
        repository = new MoviesRepository(application);

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

    }

    public void updatePath(String path) {
        this.pathLiveData.setValue(path);
        SharedPrefHelper.saveSortSelectedPath(application, path);
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
}
