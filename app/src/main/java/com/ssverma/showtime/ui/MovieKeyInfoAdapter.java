package com.ssverma.showtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssverma.showtime.R;
import com.ssverma.showtime.model.MovieKeyInfo;

import java.util.List;

public class MovieKeyInfoAdapter extends RecyclerView.Adapter<MovieKeyInfoAdapter.ViewHolder> {

    private List<MovieKeyInfo> listKeyInfo;
    private IRecyclerViewItemClickListener recyclerViewItemClickListener;

    MovieKeyInfoAdapter(List<MovieKeyInfo> listKeyInfo) {
        this.listKeyInfo = listKeyInfo;
    }

    public void setRecyclerViewItemClickListener(IRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_key_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return listKeyInfo == null ? 0 : listKeyInfo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivInfoIcon;
        private TextView tvInfoValue;

        ViewHolder(View itemView) {
            super(itemView);
            tvInfoValue = itemView.findViewById(R.id.tv_info_value);
            ivInfoIcon = itemView.findViewById(R.id.iv_info_icon);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            MovieKeyInfo current = listKeyInfo.get(position);
            tvInfoValue.setText(current.getValue());
            ivInfoIcon.setImageResource(current.getIcon());
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
