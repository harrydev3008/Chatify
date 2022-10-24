package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.hisu.zola.R;

public class HappyBirthdayDialog {

    private Dialog dialog;
    private final Context context;
    private final int gravity;

    private ImageButton btnClose;

    public HappyBirthdayDialog(Context context, int gravity) {
        this.context = context;
        this.gravity = gravity;
        initDialog();
    }

    public Dialog getDialog() {
        return dialog;
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_send_otp_dialog);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window == null)
            return;

        initUIComponent();

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
    }

    private void initUIComponent() {
        btnClose = dialog.findViewById(R.id.iBtn_close_pop_up);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void addActionForBtnClosePopup(View.OnClickListener onClickListener) {
        btnClose.setOnClickListener(onClickListener);
    }
}