package com.ssverma.showtime.ui.listing;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ssverma.showtime.R;
import com.ssverma.showtime.data.NetworkState;
import com.ssverma.showtime.data.SharedPrefHelper;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.SortOptions;
import com.ssverma.showtime.ui.IRecyclerViewItemClickListener;
import com.ssverma.showtime.ui.MovieDetailsActivity;

import java.util.List;

public class MoviesListingActivity extends AppCompatActivity {

    private MoviesListingViewModel viewModel;
    private MoviesListingAdapter moviesAdapter;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MoviesListingActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_listing);
        initViewModel();
        setUpToolbar();
        setUpContents();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MoviesListingViewModel.class);
    }

    private void toggleProgressbarVisibility(boolean shouldShow) {
        findViewById(R.id.progress_bar).setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    private void toggleMainRetryAction(boolean shouldShow) {
        LinearLayout llErrorState = findViewById(R.id.ll_error_state);
        final ProgressBar progressBar = llErrorState.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        if (!shouldShow) {
            llErrorState.setVisibility(View.GONE);
            return;
        }

        llErrorState.setVisibility(View.VISIBLE);
        final Button btnRetry = llErrorState.findViewById(R.id.btn_retry);
        btnRetry.setVisibility(View.VISIBLE);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.updatePath(viewModel.getLastSelectedSortPath());
                progressBar.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.GONE);
            }
        });
    }

    private void setUpContents() {
        toggleMainRetryAction(false);
        toggleEmptyState(false);

        final RecyclerView rvMovies = findViewById(R.id.rv_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMovies.setLayoutManager(gridLayoutManager);

        moviesAdapter = new MoviesListingAdapter();
        rvMovies.setAdapter(moviesAdapter);

        moviesAdapter.setRecyclerViewItemClickListener(new IRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View clickedView, int position) {
                MovieDetailsActivity.launch(MoviesListingActivity.this,
                        moviesAdapter.getCurrentList().get(position), clickedView);
                overridePendingTransition(0, 0);
            }
        });

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (moviesAdapter.getItemViewType(position) == MoviesListingAdapter.VIEW_TYPE_LOADING) {
                    return 2;
                }
                return 1;
            }
        });

        viewModel.updatePath(viewModel.getLastSelectedSortPath());
        viewModel.getMovies().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> movies) {
                moviesAdapter.submitList(movies);

                if (movies == null || movies.isEmpty()) {
                    toggleEmptyState(true);
                }
            }
        });

        viewModel.getInitialLoadState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState initialLoadState) {
                toggleEmptyState(false);
                if (initialLoadState == null || initialLoadState.getStatus() == null) {
                    toggleProgressbarVisibility(false);
                    toggleMainRetryAction(true);
                    return;
                }

                if (initialLoadState.getStatus() == NetworkState.Status.SUCCESS) {
                    toggleProgressbarVisibility(false);
                    toggleMainRetryAction(false);
                    return;
                }

                if (initialLoadState.getStatus() == NetworkState.Status.FAILED) {
                    toggleProgressbarVisibility(false);
                    toggleMainRetryAction(true);
                }

            }
        });

        viewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                moviesAdapter.setNetworkState(networkState);
            }
        });

    }

    private void toggleEmptyState(boolean shouldShow) {
        findViewById(R.id.tv_empty_state).setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_logo);
        setSupportActionBar(toolbar);
    }

    private void popupSortOptionsDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_sort_options);
        dialog.show();

        RecyclerView rvSortOptions = dialog.findViewById(R.id.rv_sort_options);
        rvSortOptions.setLayoutManager(new LinearLayoutManager(this));
        rvSortOptions.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        final List<SortOptions> listSortOptions = viewModel.getSortOptions();

        SortOptionsAdapter sortOptionsAdapter = new SortOptionsAdapter(listSortOptions, viewModel.getLastSelectedSortPath());
        rvSortOptions.setAdapter(sortOptionsAdapter);

        sortOptionsAdapter.setRecyclerViewItemClickListener(new IRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View clickedView, int position) {
                SortOptions clickedSortOption = listSortOptions.get(position);
                if (clickedSortOption.getSortOptionPath().equals(SharedPrefHelper.getLastSortSelectedPath(MoviesListingActivity.this))) {
                    dialog.dismiss();
                    return;
                }

                moviesAdapter.submitList(null);
                toggleProgressbarVisibility(true);
                viewModel.updatePath(listSortOptions.get(position).getSortOptionPath());
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                popupSortOptionsDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
