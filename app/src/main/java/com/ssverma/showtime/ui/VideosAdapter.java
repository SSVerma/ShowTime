package com.ssverma.showtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Video;
import com.ssverma.showtime.utils.AppUtility;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private List<Video> listVideos;
    private IRecyclerViewItemClickListener recyclerViewItemClickListener;

    VideosAdapter(List<Video> listVideos) {
        this.listVideos = listVideos;
    }

    public void setRecyclerViewItemClickListener(IRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_videos, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return listVideos == null ? 0 : listVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivThumbnail;
        private TextView tvLanguage;
        private TextView tvSize;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvLanguage = itemView.findViewById(R.id.tv_language);
            tvSize = itemView.findViewById(R.id.tv_size);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Video currentVideo = listVideos.get(position);
            tvLanguage.setText(currentVideo.getLanguage());
            tvSize.setText(String.valueOf(currentVideo.getSize()));

            Glide.with(itemView.getContext())
                    .setDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .load(AppUtility.buildThumbnailUrl(currentVideo.getVideoId()))
                    .into(ivThumbnail);
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
