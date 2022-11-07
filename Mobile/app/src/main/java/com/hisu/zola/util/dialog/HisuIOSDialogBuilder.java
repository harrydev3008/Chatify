package com.hisu.zola.util.dialog;

import android.content.Context;

import com.hisu.zola.listeners.HisuIOSDialogClickListener;

public class HisuIOSDialogBuilder {

    private final Context context;
    private int gravity;
    private HisuIOSDialogClickListener negativeListener;
    private HisuIOSDialogClickListener positiveListener;
    private HisuIOSDialogClickListener cancelListener;

    public HisuIOSDialogBuilder(Context context) {
        this.context = context;
    }

    public HisuIOSDialogBuilder setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public HisuIOSDialogBuilder setNegativeListener(HisuIOSDialogClickListener negativeListener) {
        this.negativeListener = negativeListener;
        return this;
    }

    public HisuIOSDialogBuilder setPositiveListener(HisuIOSDialogClickListener positiveListener) {
        this.positiveListener = positiveListener;
        return this;
    }

    public HisuIOSDialogBuilder setCancelListener(HisuIOSDialogClickListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    public HisuIOSDialog build() {
        HisuIOSDialog dialog = new HisuIOSDialog(context, gravity);
        dialog.setNegativeListener(this.negativeListener);
        dialog.setPositiveListener(this.positiveListener);
        dialog.setCancelListener(this.cancelListener);
        return dialog;
    }
}