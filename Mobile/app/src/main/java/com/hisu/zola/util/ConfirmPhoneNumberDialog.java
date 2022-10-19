package com.hisu.zola.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hisu.zola.R;

public class ConfirmPhoneNumberDialog {

    private Dialog dialog;
    private final Context context;
    private final int gravity;

    private TextView tvPhoneNumber, tvChange, tvConfirm;

    public ConfirmPhoneNumberDialog(Context context, int gravity) {
        this.context = context;
        this.gravity = gravity;
        initDialog();
    }

    public void setNewPhoneNumber(String newPhoneNumber) {
        this.tvPhoneNumber.setText(newPhoneNumber);
    }

    public String getNewPhoneNumber() {
        return tvPhoneNumber.getText().toString();
    }

    public Dialog getDialog() {
        return dialog;
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_change_phone_number);
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
        tvPhoneNumber = dialog.findViewById(R.id.tv_new_phone_no);
        tvChange = dialog.findViewById(R.id.tv_change);
        tvConfirm = dialog.findViewById(R.id.tv_confirm);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void addActionForBtnChange(View.OnClickListener onClickListener) {
        tvChange.setOnClickListener(onClickListener);
    }

    public void addActionForBtnConfirm(View.OnClickListener onClickListener) {
        tvConfirm.setOnClickListener(onClickListener);
    }
}