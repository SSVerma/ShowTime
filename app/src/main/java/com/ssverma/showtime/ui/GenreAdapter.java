package com.ssverma.showtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private List<Genre> listGenre;

    public GenreAdapter(List<Genre> listGenre) {
        this.listGenre = listGenre;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_genre, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return listGenre == null ? 0 : listGenre.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGenre;

        ViewHolder(View itemView) {
            super(itemView);
            tvGenre = itemView.findViewById(R.id.tv_genre);
        }

        void bind(int position) {
            tvGenre.setText(listGenre.get(position).getName());
        }
    }
}
