package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.databinding.LayoutSentFileItemChildBinding;

import java.util.List;

public class SentFileItemAdapter extends RecyclerView.Adapter<SentFileItemAdapter.SentFileItemChildViewHolder> {

    private List<Media> imageURLs;
    private final Context context;

    public SentFileItemAdapter(Context context) {
        this.context = context;
    }

    public void setImageURLs(List<Media> imageURLs) {
        this.imageURLs = imageURLs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SentFileItemChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SentFileItemChildViewHolder(
                LayoutSentFileItemChildBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SentFileItemChildViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Glide.with(context).asBitmap().load(imageURLs.get(position).getUrl())
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_img_place_holder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.binding.imvSentFileImg.setImageBitmap(resource);
                        holder.binding.imvSentFileImg.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return imageURLs != null ? imageURLs.size() : 0;
    }

    public static class SentFileItemChildViewHolder extends RecyclerView.ViewHolder {

        private final LayoutSentFileItemChildBinding binding;

        public SentFileItemChildViewHolder(@NonNull LayoutSentFileItemChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}