package com.ssverma.showtime.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

public class Listing<T> {

    private final LiveData<PagedList<T>> pagedList;
    private final LiveData<NetworkState> networkState;
    private final LiveData<NetworkState> initialLoadState;

    public Listing(LiveData<PagedList<T>> pagedList, LiveData<NetworkState> networkState, LiveData<NetworkState> initialLoadState) {
        this.pagedList = pagedList;
        this.networkState = networkState;
        this.initialLoadState = initialLoadState;
    }

    public LiveData<PagedList<T>> getPagedList() {
        return pagedList;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoadState() {
        return initialLoadState;
    }
}
