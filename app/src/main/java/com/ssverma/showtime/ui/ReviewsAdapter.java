package com.ssverma.showtime.ui;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Review;

public class ReviewsAdapter extends PagedListAdapter<Review, ReviewsAdapter.ViewHolder> {

    public static final int MAX_ITEMS_TO_SHOW = 3;

    private static final DiffUtil.ItemCallback<Review> DIFF_CALLBACK = new DiffUtil.ItemCallback<Review>() {
        @Override
        public boolean areItemsTheSame(Review oldItem, Review newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Review oldItem, Review newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final boolean shouldShowAllItems;

    ReviewsAdapter(boolean shouldShowAllItems) {
        super(DIFF_CALLBACK);
        this.shouldShowAllItems = shouldShowAllItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_review, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        if (shouldShowAllItems) {
            return super.getItemCount();
        }
        return super.getItemCount() <= MAX_ITEMS_TO_SHOW ? super.getItemCount() : MAX_ITEMS_TO_SHOW;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvReview;
        private TextView tvAuthor;
        private boolean isExpanded;

        ViewHolder(View itemView) {
            super(itemView);
            tvReview = itemView.findViewById(R.id.tv_review);
            tvAuthor = itemView.findViewById(R.id.tv_author);

            itemView.setOnClickListener(this);
        }

        void bind(Review review) {
            if (review == null) {
                return;
            }
            tvReview.setText(review.getContent());
            tvAuthor.setText(review.getAuthor());
        }

        @Override
        public void onClick(View view) {
            if (isExpanded) {
                tvReview.setMaxLines(3);
                tvReview.setEllipsize(TextUtils.TruncateAt.END);
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.white));
            } else {
                tvReview.setMaxLines(200);
                tvReview.setEllipsize(null);
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorSelectedReview));
            }
            isExpanded = !isExpanded;
        }
    }
}
