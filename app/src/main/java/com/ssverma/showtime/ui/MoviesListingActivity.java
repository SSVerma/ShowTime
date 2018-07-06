package com.ssverma.showtime.ui;

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

import com.ssverma.showtime.R;
import com.ssverma.showtime.data.SharedPrefHelper;
import com.ssverma.showtime.model.Movie;

import java.util.Arrays;
import java.util.List;

public class MoviesListingActivity extends AppCompatActivity {

    private int selectedSortIndex;
    private MoviesViewModel viewModel;

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
        viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
    }

    private void toggleProgressbarVisibility(boolean shouldShow) {
        findViewById(R.id.progress_bar).setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    private void toggleMainRetryAction(boolean shouldShow) {
        LinearLayout llErrorState = findViewById(R.id.ll_error_state);

        if (!shouldShow) {
            llErrorState.setVisibility(View.GONE);
            return;
        }

        llErrorState.setVisibility(View.VISIBLE);
        Button btnRetry = llErrorState.findViewById(R.id.btn_retry);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }

    private void setUpContents() {

        selectedSortIndex = SharedPrefHelper.getSortSelectedIndex(this);

        final RecyclerView rvMovies = findViewById(R.id.rv_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMovies.setLayoutManager(gridLayoutManager);

        final MoviesListingAdapter moviesAdapter = new MoviesListingAdapter();
        rvMovies.setAdapter(moviesAdapter);

        viewModel.updateFilter("popular");

        toggleMainRetryAction(false);

        viewModel.getMovies().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> movies) {
                moviesAdapter.submitList(movies);
                toggleProgressbarVisibility(false);
            }
        });

        moviesAdapter.setRecyclerViewItemClickListener(new IRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View clickedView, int position) {
                MovieDetailsActivity.launch(MoviesListingActivity.this,
                        moviesAdapter.getCurrentList().get(position), clickedView);
            }
        });

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

        SortOptionsAdapter sortOptionsAdapter = new SortOptionsAdapter(prepareSortOptions(), selectedSortIndex);
        rvSortOptions.setAdapter(sortOptionsAdapter);

        sortOptionsAdapter.setRecyclerViewItemClickListener(new IRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View clickedView, int position) {
                if (position == SharedPrefHelper.getSortSelectedIndex(MoviesListingActivity.this)) {
                    dialog.dismiss();
                    return;
                }

                selectedSortIndex = position;
                SharedPrefHelper.saveSortSelectedIndex(MoviesListingActivity.this, position);
                dialog.dismiss();
            }
        });

    }

    private List<String> prepareSortOptions() {
        String[] sortOptions = {
                "Most Popular",
                "Top Rated"
        };
        return Arrays.asList(sortOptions);
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
