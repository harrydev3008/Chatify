package com.hisu.zola.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.databinding.LayoutStarterSliderBinding;
import com.hisu.zola.model.StarterSliderItem;

import java.util.List;

public class StarterSliderAdapter extends
        RecyclerView.Adapter<StarterSliderAdapter.StartSliderViewHolder> {

    private List<StarterSliderItem> mImages;

    public StarterSliderAdapter(List<StarterSliderItem> photos) {
        this.mImages = photos;
    }

    @NonNull
    @Override
    public StartSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StartSliderViewHolder(
                LayoutStarterSliderBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StartSliderViewHolder holder, int position) {
        StarterSliderItem sliderItem = mImages.get(position);
        holder.mBinding.ivCoverPhoto.setImageResource(sliderItem.getCoverImageID());
        holder.mBinding.tvFeature.setText(sliderItem.getFeature());
        holder.mBinding.tvFeatureDesc.setText(sliderItem.getFeatureDesc());
    }

    @Override
    public int getItemCount() {
        return mImages != null ? mImages.size() : 0;
    }

    public static class StartSliderViewHolder extends RecyclerView.ViewHolder {

        private LayoutStarterSliderBinding mBinding;

        public StartSliderViewHolder(@NonNull LayoutStarterSliderBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}