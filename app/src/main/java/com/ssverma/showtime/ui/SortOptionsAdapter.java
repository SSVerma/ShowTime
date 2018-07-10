package com.ssverma.showtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssverma.showtime.R;
import com.ssverma.showtime.model.SortOptions;

import java.util.List;

public class SortOptionsAdapter extends RecyclerView.Adapter<SortOptionsAdapter.ViewHolder> {

    private List<SortOptions> listSortOptions;
    private IRecyclerViewItemClickListener recyclerViewItemClickListener;
    private String selectedPath;

    SortOptionsAdapter(List<SortOptions> listSortOptions, String selectedPath) {
        this.listSortOptions = listSortOptions;
        this.selectedPath = selectedPath;
    }

    public void setRecyclerViewItemClickListener(IRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_sort_options, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SortOptions current = listSortOptions.get(position);
        holder.tvSortOption.setText(current.getSortOptionLabel());
        if (current.getSortOptionPath().equals(selectedPath)) {
            holder.ivCheckIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheckIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listSortOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvSortOption;
        ImageView ivCheckIcon;

        ViewHolder(View itemView) {
            super(itemView);

            tvSortOption = itemView.findViewById(R.id.tv_sort_option);
            ivCheckIcon = itemView.findViewById(R.id.iv_check_icon);
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
}
