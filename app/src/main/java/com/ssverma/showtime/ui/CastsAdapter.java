package com.ssverma.showtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssverma.showtime.R;
import com.ssverma.showtime.model.Cast;
import com.ssverma.showtime.utils.AppUtility;

import java.util.List;

public class CastsAdapter extends RecyclerView.Adapter<CastsAdapter.ViewHolder> {

    private List<Cast> listCasts;

    CastsAdapter(List<Cast> listCasts) {
        this.listCasts = listCasts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_cast, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return listCasts == null ? 0 : listCasts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCastName;
        private ImageView ivCast;

        ViewHolder(View itemView) {
            super(itemView);
            tvCastName = itemView.findViewById(R.id.tv_cast);
            ivCast = itemView.findViewById(R.id.iv_cast);
        }

        void bind(int position) {
            tvCastName.setText(listCasts.get(position).getName());
            Picasso.get()
                    .load(AppUtility.buildCastImageUrl(listCasts.get(position).getProfilePath()))
                    .into(ivCast);

        }
    }
}
