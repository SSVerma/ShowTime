package com.ssverma.showtime.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.ssverma.showtime.BuildConfig;
import com.ssverma.showtime.R;
import com.ssverma.showtime.api.ApiUtils;
import com.ssverma.showtime.data.SharedPrefHelper;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.model.MovieResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListingActivity extends AppCompatActivity {

    private List<Movie> listMovies;
    private MoviesAdapter moviesAdapter;
    private int totalPages;
    private int currentPage = 1;
    private int selectedSortIndex;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MoviesListingActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_listing);
        setUpToolbar();
        setUpContents();
        makeMoviesApiRequest();
    }

    private void makeMoviesApiRequest() {
        moviesAdapter.setLoading(true);

        if (currentPage == 1) {
            toggleProgressbarVisibility(true);
            toggleMainRetryAction(false);
        }

        Call<MovieResponse> apiCall;

        switch (selectedSortIndex) {
            default:
            case 0:
                apiCall = ApiUtils.getTmdbService().getPopularMovies(currentPage);
                break;
            case 1:
                apiCall = ApiUtils.getTmdbService().getTopRatedMovies(currentPage);
                break;
        }

        apiCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                t.printStackTrace();
                toggleProgressbarVisibility(false);
                toggleMainRetryAction(currentPage == 1);
            }
        });

    }

    private void toggleProgressbarVisibility(boolean shouldShow) {
        findViewById(R.id.progress_bar).setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    private void handleResponse(Response<MovieResponse> response) {

        if (currentPage == 1) {
            toggleProgressbarVisibility(false);
        }

        if (response.body() == null) {
            toggleMainRetryAction(currentPage == 1);
            return;
        }

        if (currentPage == 1) {
            this.totalPages = response.body().getTotalPages();
        }

        List<Movie> movies = response.body().getMoviesList();

        if (!listMovies.isEmpty()) {
            int progressbarIndex = listMovies.size() - 1;
            listMovies.remove(progressbarIndex);
            moviesAdapter.notifyItemRemoved(progressbarIndex);
        }

        int prevSize = listMovies.size();
        listMovies.addAll(movies);
        moviesAdapter.notifyItemRangeInserted(prevSize, listMovies.size());
        moviesAdapter.setLoading(false);
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
                makeMoviesApiRequest();
            }
        });
    }

    private void setUpContents() {

        listMovies = new ArrayList<>();
        selectedSortIndex = SharedPrefHelper.getSortSelectedIndex(this);

        final RecyclerView rvMovies = findViewById(R.id.rv_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMovies.setLayoutManager(gridLayoutManager);

        moviesAdapter = new MoviesAdapter(rvMovies, listMovies, new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (currentPage >= totalPages) {
                    return;
                }

                currentPage++;

                listMovies.add(null);//For loading indicator
                rvMovies.post(new Runnable() {
                    @Override
                    public void run() {
                        moviesAdapter.notifyItemInserted(listMovies.size() - 1);
                    }
                });

                makeMoviesApiRequest();
            }
        });

        rvMovies.setAdapter(moviesAdapter);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return moviesAdapter.getItemViewType(position)
                        == MoviesAdapter.VIEW_TYPE_LOADING ? 2 : 1;
            }
        });

        moviesAdapter.setRecyclerViewItemClickListener(new IRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View clickedView, int position) {
                MovieDetailsActivity.launch(MoviesListingActivity.this, listMovies.get(position), clickedView);
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

                currentPage = 1;
                MoviesListingActivity.this.listMovies.clear();
                moviesAdapter.notifyDataSetChanged();

                selectedSortIndex = position;
                SharedPrefHelper.saveSortSelectedIndex(MoviesListingActivity.this, position);
                makeMoviesApiRequest();
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
