package com.ssverma.showtime.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.ssverma.showtime.model.Review;

public class ReviewDataSourceFactory extends DataSource.Factory<Integer, Review> {

    private final int movieId;
    private MutableLiveData<ReviewDataSource> dataSourceLiveData = new MutableLiveData<>();

    ReviewDataSourceFactory(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public DataSource<Integer, Review> create() {
        ReviewDataSource dataSource = new ReviewDataSource(movieId);
        dataSourceLiveData.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<ReviewDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
