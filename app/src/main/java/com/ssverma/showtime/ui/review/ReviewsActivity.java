package com.ssverma.showtime.ui.review;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ssverma.showtime.R;
import com.ssverma.showtime.data.NetworkState;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.ui.MovieDetailsViewModel;

public class ReviewsActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE_ID = "extra_movie_id";

    public static void launch(Context context, int movieId) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        intent.putExtra(EXTRA_MOVIE_ID, movieId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setUpToolbar();
        setUpContents();
    }

    private void setUpContents() {

        final ProgressBar pbLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        final RecyclerView rvReviews = findViewById(R.id.rv_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setNestedScrollingEnabled(false);
        rvReviews.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        final ReviewsAdapter reviewsAdapter = new ReviewsAdapter(true);
        rvReviews.setAdapter(reviewsAdapter);

        MovieDetailsViewModel viewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);

        viewModel.updateMovieId(movieId);
        viewModel.getReviews().observe(this, new Observer<PagedList<Review>>() {
            @Override
            public void onChanged(@Nullable PagedList<Review> reviews) {
                reviewsAdapter.submitList(reviews);
            }
        });

        viewModel.getInitialLoadState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState == null || networkState.getStatus() == NetworkState.Status.SUCCESS
                        || networkState.getStatus() == NetworkState.Status.FAILED) {
                    pbLoadingIndicator.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.reviews_toolbar_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reviews, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
