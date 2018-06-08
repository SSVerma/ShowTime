package com.ssverma.showtime.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE = "extra_movie";
    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342";
    private static final String BASE_BACKDROP_PATH = "http://image.tmdb.org/t/p/w500";

    public static void launch(Activity activity, Movie movie, View clickedView) {
        Intent intent = new Intent(activity, MovieDetailsActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                clickedView, activity.getString(R.string.transition_poster));
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setUpToolbar();
        setUpContents();
    }

    private void setUpContents() {
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

        /*Backdrop image*/
        ImageView ivBackdrop = findViewById(R.id.iv_backdrop);
        Picasso.get()
                .load(BASE_BACKDROP_PATH + movie.getBackdropPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivBackdrop);

        /*Poster image*/
        ImageView ivPoster = findViewById(R.id.iv_poster);
        Picasso.get()
                .load(BASE_POSTER_PATH + movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivPoster);

        /*Title*/
        TextView tvMovieTitle = findViewById(R.id.tv_movie_title);
        tvMovieTitle.setText(movie.getMovieTitle());

        /*Release date*/
        TextView tvReleaseDate = findViewById(R.id.tv_release_date);
        tvReleaseDate.setText(movie.getReleaseDate());

        /*Ratings*/
        TextView tvRatings = findViewById(R.id.tv_rating);
        String formattedRating = movie.getUserRating() + " / 10";
        tvRatings.setText(formattedRating);

        /*Synopsis*/
        TextView tvSynopsis = findViewById(R.id.tv_synopsis);
        tvSynopsis.setText(movie.getPlotSynopsis());
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.empty_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
