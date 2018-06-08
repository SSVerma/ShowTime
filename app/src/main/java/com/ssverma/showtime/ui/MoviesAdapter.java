package com.ssverma.showtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Movie;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;
    private static final int VISIBLE_THRESHOLD = 4;
    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342";
    private boolean isLoading;

    private List<Movie> listMovies;
    private IRecyclerViewItemClickListener recyclerViewItemClickListener;

    public MoviesAdapter(RecyclerView rvMovies, List<Movie> listMovies, ILoadMoreListener loadMoreListener) {
        this.listMovies = listMovies;
        setUpRecyclerViewScrollListener(rvMovies, loadMoreListener);
    }

    private void setUpRecyclerViewScrollListener(RecyclerView rvMovies, final ILoadMoreListener loadMoreListener) {

        if (loadMoreListener == null) {
            throw new NullPointerException("LoadMoreListener must not be mull");
        }

        final GridLayoutManager layoutManager = (GridLayoutManager) rvMovies.getLayoutManager();

        rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItems = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                boolean shouldTriggerLoadMore = totalItems <= lastVisibleItem + 1 + VISIBLE_THRESHOLD;

                if (!isLoading && shouldTriggerLoadMore) {
                    loadMoreListener.onLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setRecyclerViewItemClickListener(IRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_movie, parent, false);
                return new ItemViewHolder(itemView);

            case VIEW_TYPE_LOADING:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_loading, parent, false);
                return new LoadingViewHolder(itemView);

                default:
                    throw new IllegalArgumentException("Invalid viewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            Movie currentMovie = listMovies.get(position);

            Picasso.get()
                    .load(BASE_POSTER_PATH + currentMovie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(itemViewHolder.ivPoster);

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listMovies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listMovies.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPoster;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (recyclerViewItemClickListener == null) {
                return;
            }
            recyclerViewItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
