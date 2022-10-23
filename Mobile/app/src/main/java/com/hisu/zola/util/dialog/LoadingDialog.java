package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.hisu.zola.R;

public class LoadingDialog {

    private Dialog dialog;
    private final Context context;
    private final int gravity;

    public LoadingDialog(Context context, int gravity) {
        this.context = context;
        this.gravity = gravity;
        initDialog();
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_loading_dialog);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window == null)
            return;

        Sprite cubes = new WanderingCubes();
        cubes.setColor(ContextCompat.getColor(context, R.color.primary_color));
        ProgressBar progressBar = dialog.findViewById(R.id.pb_loading);
        progressBar.setIndeterminateDrawable(cubes);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}