package com.ssverma.showtime.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssverma.showtime.R;
import com.ssverma.showtime.common.Resource;
import com.ssverma.showtime.model.Cast;
import com.ssverma.showtime.model.CastResponse;
import com.ssverma.showtime.model.Genre;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.MovieDetailsResponse;
import com.ssverma.showtime.model.MovieKeyInfo;
import com.ssverma.showtime.model.Review;
import com.ssverma.showtime.model.Video;
import com.ssverma.showtime.model.VideosResponse;
import com.ssverma.showtime.ui.review.ReviewsActivity;
import com.ssverma.showtime.ui.review.ReviewsAdapter;
import com.ssverma.showtime.utils.AppUtility;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE = "extra_movie";
    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342";
    private static final String BASE_BACKDROP_PATH = "http://image.tmdb.org/t/p/w500";
    private MoviesViewModel viewModel;
    private List<Video> listVideos;
    private TextView tvAddToFavorite;

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
        initViews();
        initViewModel();
        setUpToolbar();
        setUpHeaderContents();
        setUpMovieKeyInfo();
        setUpFavoriteToggle();
        setUpVideosSection();
        setUpReviewsSection();
        setUpCastsSection();
    }

    private void setUpCastsSection() {

        /*Cast section title*/
        final LinearLayout llSectionTitle = findViewById(R.id.ll_casts);
        llSectionTitle.setVisibility(View.GONE);
        TextView tvSectionTitle = llSectionTitle.findViewById(R.id.tv_section_title);
        tvSectionTitle.setText(getString(R.string.label_casts));

        RecyclerView rvCasts = findViewById(R.id.rv_casts);
        rvCasts.setLayoutManager(new GridLayoutManager(this, 3));
        rvCasts.setNestedScrollingEnabled(false);

        final List<Cast> listCasts = new ArrayList<>();

        final CastsAdapter castsAdapter = new CastsAdapter(listCasts);
        rvCasts.setAdapter(castsAdapter);

        viewModel.getCasts().observe(this, new Observer<Resource<CastResponse>>() {
            @Override
            public void onChanged(@Nullable Resource<CastResponse> resource) {
                if (resource == null || !resource.isSuccess()) {
                    return;
                }

                listCasts.clear();
                listCasts.addAll(resource.getData().getCasts());
                castsAdapter.notifyDataSetChanged();
                llSectionTitle.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setUpMovieKeyInfo() {
        try {

            /*Key Info*/
            RecyclerView rvKeyInfo = findViewById(R.id.rv_key_info);
            rvKeyInfo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvKeyInfo.setNestedScrollingEnabled(false);

            final List<MovieKeyInfo> listKeyInfo = new ArrayList<>();

            final MovieKeyInfoAdapter keyInfoAdapter = new MovieKeyInfoAdapter(listKeyInfo);
            rvKeyInfo.setAdapter(keyInfoAdapter);

            /*Genre*/
            RecyclerView rvGenre = findViewById(R.id.rv_genre);
            rvGenre.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvGenre.setNestedScrollingEnabled(false);

            final List<Genre> listGenres = new ArrayList<>();

            final GenreAdapter genreAdapter = new GenreAdapter(listGenres);
            rvGenre.setAdapter(genreAdapter);

            /*Movie tag-line*/
            final TextView tvTagLine = findViewById(R.id.tv_tag_line);

            viewModel.getMovieDetails().observe(this, new Observer<Resource<MovieDetailsResponse>>() {
                @Override
                public void onChanged(@Nullable Resource<MovieDetailsResponse> resource) {
                    if (resource == null || !resource.isSuccess()) {
                        return;
                    }

                    listKeyInfo.clear();
                    listKeyInfo.addAll(viewModel.getMovieKeyInfo(resource.getData()));
                    keyInfoAdapter.notifyDataSetChanged();

                    listGenres.clear();
                    listGenres.addAll(resource.getData().getGenres());
                    genreAdapter.notifyDataSetChanged();

                    tvTagLine.setText(resource.getData().getTagLine());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpFavoriteToggle() {
        viewModel.isMovieFavorite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFavoriteMovie) {
                if (isFavoriteMovie == null) {
                    tvAddToFavorite.setText(getString(R.string.add_to_favorite));
                    tvAddToFavorite.setBackgroundResource(R.drawable.background_add_to_favorite);
                    tvAddToFavorite.setTextColor(getResources().getColor(android.R.color.black));
                    tvAddToFavorite.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_border, 0);
                    return;
                }

                tvAddToFavorite.setText(getString(R.string.favorite));
                tvAddToFavorite.setBackgroundResource(R.drawable.background_favorite);
                tvAddToFavorite.setTextColor(getResources().getColor(android.R.color.white));
                tvAddToFavorite.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite, 0);
            }
        });

        final Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

        tvAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAddToFavorite.getText().toString().equals(getString(R.string.add_to_favorite))) {
                    viewModel.addToFavorite(movie);
                } else {
                    viewModel.removeFromFavorite(movie.getId());
                }
            }
        });

    }

    private void initViews() {
        tvAddToFavorite = findViewById(R.id.tv_add_to_favorite_action);
    }

    private void setUpReviewsSection() {

        /*Reviews section title*/
        final LinearLayout llSectionTitle = findViewById(R.id.ll_reviews_section_title_holder);
        llSectionTitle.setVisibility(View.GONE);
        TextView tvSectionTitle = llSectionTitle.findViewById(R.id.tv_section_title);
        tvSectionTitle.setText(getString(R.string.label_reviews));

        /*Reviews*/
        final CardView cvReviews = findViewById(R.id.cv_reviews);
        cvReviews.setVisibility(View.GONE);
        final TextView tvViewAll = cvReviews.findViewById(R.id.tv_view_all);
        final RecyclerView rvReviews = cvReviews.findViewById(R.id.rv_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setNestedScrollingEnabled(false);
        rvReviews.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        final ReviewsAdapter reviewsAdapter = new ReviewsAdapter(false);
        rvReviews.setAdapter(reviewsAdapter);

        final Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewsActivity.launch(MovieDetailsActivity.this, movie.getId());
            }
        });

        viewModel.getReviews().observe(this, new Observer<PagedList<Review>>() {
            boolean isFirstTime = true;

            @Override
            public void onChanged(@Nullable PagedList<Review> reviews) {
                reviewsAdapter.submitList(reviews);

                if (isFirstTime && !(reviews == null || reviews.isEmpty())) {
                    isFirstTime = false;

                    llSectionTitle.setVisibility(View.VISIBLE);
                    cvReviews.setVisibility(View.VISIBLE);

                    if (reviews.size() <= ReviewsAdapter.MAX_ITEMS_TO_SHOW) {
                        tvViewAll.setVisibility(View.GONE);
                    }

                }
            }
        });

    }

    private void setUpVideosSection() {

        listVideos = new ArrayList<>();

        /*Videos section title*/
        final LinearLayout llSectionTitle = findViewById(R.id.ll_videos_section_title_holder);
        llSectionTitle.setVisibility(View.GONE);
        TextView tvSectionTitle = llSectionTitle.findViewById(R.id.tv_section_title);
        tvSectionTitle.setText(getString(R.string.label_videos));

        /*Videos*/
        final RecyclerView rvVideos = findViewById(R.id.rv_videos);
        rvVideos.setVisibility(View.GONE);
        rvVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvVideos.setNestedScrollingEnabled(false);

        final VideosAdapter videosAdapter = new VideosAdapter(listVideos);
        rvVideos.setAdapter(videosAdapter);

        videosAdapter.setRecyclerViewItemClickListener(new IRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View clickedView, int position) {
                AppUtility.launchYoutube(MovieDetailsActivity.this, listVideos.get(position).getVideoId());
            }
        });

        viewModel.getVideos().observe(this, new Observer<Resource<VideosResponse>>() {
            @Override
            public void onChanged(@Nullable Resource<VideosResponse> resource) {
                if (resource == null) {
                    return;
                }

                if (!resource.isSuccess()) {
                    return;
                }

                if (resource.getData() == null || resource.getData().getVideos() == null) {
                    return;
                }

                llSectionTitle.setVisibility(View.VISIBLE);
                rvVideos.setVisibility(View.VISIBLE);

                MovieDetailsActivity.this.listVideos.clear();
                MovieDetailsActivity.this.listVideos.addAll(resource.getData().getVideos());
                videosAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        viewModel.updateMovieId(movie.getId());
    }

    private void setUpHeaderContents() {
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

        /*Synopsis label*/
        LinearLayout llSectionTitle = findViewById(R.id.ll_synopsis_section_title_holder);
        TextView tvSectionTitle = llSectionTitle.findViewById(R.id.tv_section_title);
        tvSectionTitle.setText(getString(R.string.synopsis_label));

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
