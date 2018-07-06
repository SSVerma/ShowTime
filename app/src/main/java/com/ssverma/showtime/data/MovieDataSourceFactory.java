package com.ssverma.showtime.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.ssverma.showtime.model.Movie;

public class MovieDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    private final String filter;
    private MutableLiveData<MovieDataSource> dataSourceLiveData = new MutableLiveData<>();

    MovieDataSourceFactory(String filter) {
        this.filter = filter;
    }

    @Override
    public DataSource<Integer, Movie> create() {
        MovieDataSource dataSource = new MovieDataSource(filter);
        dataSourceLiveData.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<MovieDataSource> getDataSource() {
        return dataSourceLiveData;
    }
}
