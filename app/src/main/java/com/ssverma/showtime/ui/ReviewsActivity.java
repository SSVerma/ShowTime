package com.ssverma.showtime.ui;

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
import android.view.View;
import android.widget.ProgressBar;

import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Review;

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

        MoviesViewModel viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);

        viewModel.setMovieId(movieId);
        viewModel.getReviews().observe(this, new Observer<PagedList<Review>>() {
            boolean isFirstTime = true;

            @Override
            public void onChanged(@Nullable PagedList<Review> reviews) {
                reviewsAdapter.submitList(reviews);
                if (isFirstTime) {
                    isFirstTime = false;
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
}
