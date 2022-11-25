package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutViewImageBinding;
import com.hisu.zola.util.network.NetworkUtil;

public class ViewImageDialog {
    private Dialog dialog;
    private final Context context;
    private final int gravity;
    private LayoutViewImageBinding binding;
    private final String imageUrl;

    public ViewImageDialog(Context context, String imageUrl, int gravity) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.gravity = gravity;
        initDialog();
    }

    private void initDialog() {
        binding = LayoutViewImageBinding.inflate(LayoutInflater.from(context), null, false);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        Window window = dialog.getWindow();

        if (window == null)
            return;

        initUIComponent();

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
    }

    private void initUIComponent() {
        Glide.with(context).asBitmap().load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_img_place_holder))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.ptvImage.setImageBitmap(resource);
                        binding.ptvImage.setVisibility(View.VISIBLE);
                    }
                });

        binding.btnClose.setOnClickListener(view -> {
            dismissDialog();
        });

        binding.btnDownload.setOnClickListener(view -> {
            NetworkUtil.downloadImage(context, imageUrl, imageUrl.substring(imageUrl.lastIndexOf("/") + 1), binding.btnDownload);
        });
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}