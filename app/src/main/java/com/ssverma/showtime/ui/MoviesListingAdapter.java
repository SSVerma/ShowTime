package com.ssverma.showtime.ui;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Movie;

public class MoviesListingAdapter extends PagedListAdapter<Movie, MoviesListingAdapter.ViewHolder> {

    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342";
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

    protected MoviesListingAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setRecyclerViewItemClickListener(IRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_movie, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivPoster;

        ViewHolder(View itemView) {
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

            Picasso.get()
                    .load(BASE_POSTER_PATH + currentMovie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(ivPoster);
        }
    }
}
