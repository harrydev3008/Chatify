package com.hisu.zola.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.R;

import java.util.ArrayList;
import java.util.List;

public class StarterSliderAdapter extends
        RecyclerView.Adapter<StarterSliderAdapter.StartSliderViewHolder> {

    private List<Integer> photos;

    public StarterSliderAdapter(List<Integer> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public StartSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_start_screen,parent, false);

        return new StartSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartSliderViewHolder holder, int position) {
        holder.sliderImg.setImageResource(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos != null ? photos.size() : 0;
    }

    public class StartSliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView sliderImg;

        public StartSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sliderImg = itemView.findViewById(R.id.iv_slider_image);
        }
    }
}