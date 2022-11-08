package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.hisu.zola.databinding.LayoutHisuIosDialogBinding;
import com.hisu.zola.listeners.HisuIOSDialogClickListener;

public class HisuIOSDialog {

    private final Dialog dialog;
    private HisuIOSDialogClickListener negativeListener;
    private HisuIOSDialogClickListener positiveListener;
    private HisuIOSDialogClickListener cancelListener;
    private final LayoutHisuIosDialogBinding binding;

    public HisuIOSDialog(Context context, int gravity) {
        dialog = new Dialog(context);
        binding = LayoutHisuIosDialogBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window == null)
            return;

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        initEvents();
    }

    public void setNegativeListener(HisuIOSDialogClickListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public void setPositiveListener(HisuIOSDialogClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setCancelListener(HisuIOSDialogClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    private void initEvents() {
        binding.tvNegative.setOnClickListener(view -> {
            if (negativeListener != null)
                negativeListener.onClick(HisuIOSDialog.this);
        });

        binding.tvPositive.setOnClickListener(view -> {
            if (positiveListener != null)
                positiveListener.onClick(HisuIOSDialog.this);
        });

        binding.tvCancel.setOnClickListener(view -> {
            if (cancelListener != null)
                cancelListener.onClick(HisuIOSDialog.this);
        });
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}