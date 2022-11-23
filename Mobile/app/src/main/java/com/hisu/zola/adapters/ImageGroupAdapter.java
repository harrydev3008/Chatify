package com.hisu.zola.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.databinding.LayoutImageGroupItemBinding;
import com.hisu.zola.listeners.IOnItemTouchListener;

import java.util.List;

public class ImageGroupAdapter extends RecyclerView.Adapter<ImageGroupAdapter.ImageGroupViewHolder> {

    private List<Media> mediaList;
    private final Context context;
    private int mode;
    public static final int SEND_MODE = 1;
    public static final int RECEIVE_MODE = 0;
    private IOnItemTouchListener onItemTouchListener;

    public ImageGroupAdapter(Context context) {
        this.context = context;
    }

    public ImageGroupAdapter(List<Media> mediaList, Context context, int mode) {
        this.mediaList = mediaList;
        this.context = context;
        this.mode = mode;
        notifyDataSetChanged();
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    public void setOnItemTouchListener(IOnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
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

        if (mode == SEND_MODE)
            holder.binding.itemParent.setGravity(Gravity.END);
        else if (mode == RECEIVE_MODE)
            holder.binding.itemParent.setGravity(Gravity.START);

        if (mediaList.size() < 2) {
            holder.binding.rimvImageGroupItem.setMaxWidth(getDp(320));
            holder.binding.rimvImageGroupItem.setMaxHeight(getDp(200));
        } else {
            holder.binding.rimvImageGroupItem.setMaxWidth(getDp(160));
            holder.binding.rimvImageGroupItem.setMaxHeight(getDp(200));
        }

        Glide.with(context).load(media.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.rimvImageGroupItem);

        holder.binding.rimvImageGroupItem.setOnLongClickListener(view -> {
            onItemTouchListener.longPress(null, null);
            return true;
        });
    }

    public int getDp(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
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