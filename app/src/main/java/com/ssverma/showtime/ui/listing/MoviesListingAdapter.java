package com.ssverma.showtime.ui.listing;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ssverma.showtime.R;
import com.ssverma.showtime.data.NetworkState;
import com.ssverma.showtime.model.Movie;
import com.ssverma.showtime.ui.IRecyclerViewItemClickListener;
import com.ssverma.showtime.utils.AppUtility;

public class MoviesListingAdapter extends PagedListAdapter<Movie, RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_LOADING = 2;
    private static final int VIEW_TYPE_ITEM = 1;
    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.equals(newItem);
        }
    };

    private IRecyclerViewItemClickListener recyclerViewItemClickListener;
    private NetworkState networkState;

    MoviesListingAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setRecyclerViewItemClickListener(IRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                itemView = inflater.inflate(R.layout.row_item_movie, parent, false);
                return new ItemViewHolder(itemView);

            case VIEW_TYPE_LOADING:
                itemView = inflater.inflate(R.layout.row_item_loading, parent, false);
                return new LoadingViewHolder(itemView);
        }
        throw new IllegalArgumentException("Invalid viewType: " + viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.bind(position);

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.bind();
        }
    }

    @Override
    public int getItemCount() {
        if (shouldShowExtraRow()) {
            return super.getItemCount() + 1;
        }
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (shouldShowExtraRow() && position == getItemCount() - 1) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    private boolean shouldShowExtraRow() {
        return networkState != null && networkState.getStatus() != NetworkState.Status.SUCCESS;
    }

    public void setNetworkState(NetworkState newState) {
        NetworkState prevState = this.networkState;
        boolean isExtraRowAlreadyShowing = shouldShowExtraRow();
        this.networkState = newState;
        boolean shouldShowExtraRowNow = shouldShowExtraRow();

        if (isExtraRowAlreadyShowing != shouldShowExtraRowNow) {
            if (isExtraRowAlreadyShowing) {
                notifyItemRemoved(getItemCount() - 1);
            } else {
                notifyItemInserted(getItemCount() - 1);
            }
        } else if (shouldShowExtraRowNow && prevState != newState) {
            /*If error type changed*/
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivPoster;

        ItemViewHolder(View itemView) {
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

        void bind(int position) {
            Movie currentMovie = getItem(position);

            if (currentMovie == null) {
                return;
            }

            Glide.with(itemView.getContext())
                    .setDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .load(AppUtility.buildPosterUrl(currentMovie.getPosterPath()))
                    .into(ivPoster);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }

        public void bind() {
            if (networkState == null || getAdapterPosition() == 0) {
                itemView.setVisibility(View.GONE);
                return;
            }
            itemView.setVisibility(networkState.getStatus() == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);
        }
    }

}
