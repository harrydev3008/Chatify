package com.hisu.zola.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutStickerBinding;
import com.hisu.zola.listeners.IOnSendStickerListener;

import java.util.Arrays;
import java.util.List;

public class StickerBottomSheetAdapter extends RecyclerView.Adapter<StickerBottomSheetAdapter.StickerViewHolder> {

    private final List<String> stickers;
    private final Context context;
    private final IOnSendStickerListener onSendStickerListener;

    public StickerBottomSheetAdapter(Context context, IOnSendStickerListener onSendStickerListener) {
        this.context = context;
        this.onSendStickerListener = onSendStickerListener;
        stickers = Arrays.asList(context.getResources().getStringArray(R.array.stickers));
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StickerViewHolder(
                LayoutStickerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String sticker = stickers.get(position);

        Glide.with(context)
                .asBitmap().load(sticker)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.binding.imgSticker.setImageBitmap(resource);
                        holder.binding.imgSticker.setVisibility(View.VISIBLE);
                    }
                });

        holder.binding.imgSticker.setOnClickListener(view -> {
            onSendStickerListener.sendSticker(sticker);
        });
    }

    @Override
    public int getItemCount() {
        return stickers != null ? stickers.size() : 0;
    }

    public static class StickerViewHolder extends RecyclerView.ViewHolder {

        private final LayoutStickerBinding binding;

        public StickerViewHolder(@NonNull LayoutStickerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}