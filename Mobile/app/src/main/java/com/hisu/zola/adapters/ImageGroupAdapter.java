package com.hisu.zola.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.databinding.LayoutImageGroupItemBinding;

import java.util.List;

public class ImageGroupAdapter extends RecyclerView.Adapter<ImageGroupAdapter.ImageGroupViewHolder> {

    private List<Media> mediaList;
    private final Context context;

    public ImageGroupAdapter(Context context) {
        this.context = context;
    }

    public ImageGroupAdapter(List<Media> mediaList, Context context) {
        this.mediaList = mediaList;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageGroupViewHolder(
                LayoutImageGroupItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGroupViewHolder holder, int position) {
        Media media = mediaList.get(position);
        Glide.with(context).load(media.getUrl()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(holder.binding.rimvImageGroupItem);
    }

    @Override
    public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }

    public static class ImageGroupViewHolder extends RecyclerView.ViewHolder {

        private final LayoutImageGroupItemBinding binding;

        public ImageGroupViewHolder(@NonNull LayoutImageGroupItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}