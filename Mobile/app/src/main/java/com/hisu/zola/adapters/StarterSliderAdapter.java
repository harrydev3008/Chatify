package com.hisu.zola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutStartScreenBinding;

import java.util.List;

public class StarterSliderAdapter extends
        RecyclerView.Adapter<StarterSliderAdapter.StartSliderViewHolder> {

    private List<Integer> mImages;

    public StarterSliderAdapter(List<Integer> photos) {
        this.mImages = photos;
    }

    @NonNull
    @Override
    public StartSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StartSliderViewHolder(
                LayoutStartScreenBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StartSliderViewHolder holder, int position) {
        holder.mBinding.ivSliderImage.setImageResource(mImages.get(position));
    }

    @Override
    public int getItemCount() {
        return mImages != null ? mImages.size() : 0;
    }

    public static class StartSliderViewHolder extends RecyclerView.ViewHolder {

        private LayoutStartScreenBinding mBinding;

        public StartSliderViewHolder(@NonNull LayoutStartScreenBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}