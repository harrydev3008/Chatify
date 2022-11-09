package com.hisu.zola.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hisu.zola.R;
import com.hisu.zola.adapters.StickerBottomSheetAdapter;
import com.hisu.zola.databinding.LayoutStickerBottomSheetBinding;
import com.hisu.zola.listeners.IOnSendStickerListener;

import java.util.ArrayList;
import java.util.List;

public class StickerBottomSheetFragment extends BottomSheetDialogFragment {

    private IOnSendStickerListener onSendStickerListener;

    public void setOnSendStickerListener(IOnSendStickerListener onSendStickerListener) {
        this.onSendStickerListener = onSendStickerListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        LayoutStickerBottomSheetBinding binding = LayoutStickerBottomSheetBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialog.setContentView(binding.getRoot());


        StickerBottomSheetAdapter adapter = new StickerBottomSheetAdapter(getContext(), onSendStickerListener);
        binding.rvSticker.setAdapter(adapter);

        binding.rvSticker.setLayoutManager(new GridLayoutManager(getContext(), 3));

        dialog.getBehavior().setMaxHeight(700);

        return dialog;
    }
}