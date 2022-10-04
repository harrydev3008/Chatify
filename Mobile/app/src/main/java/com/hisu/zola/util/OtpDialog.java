package com.hisu.zola.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hisu.zola.R;

public class OtpDialog {

    private Dialog dialog;
    private Context context;
    private int gravity;

    private EditText edtOtp;
    private Button btnCancel, btnConfirm;
    private TextView tvOtpNotReceive;

    public OtpDialog(Context context, int gravity) {
        this.context = context;
        this.gravity = gravity;
        initDialog();
    }

    public Dialog getDialog() {
        return dialog;
    }

    public EditText getEdtOtp() {
        return edtOtp;
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_otp_dialog);
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
         edtOtp = dialog.findViewById(R.id.edt_otp);
         btnCancel = dialog.findViewById(R.id.mBtn_cancel);
         btnConfirm = dialog.findViewById(R.id.mBtn_confirm);
         tvOtpNotReceive = dialog.findViewById(R.id.tv_otp_not_receive);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public String getEditTextInput() {
        return edtOtp != null ? edtOtp.getText().toString().trim() : "";
    }

    public void addActionForBtnCancel(View.OnClickListener onClickListener) {
        btnCancel.setOnClickListener(onClickListener);
    }

    public void addActionForBtnConfirm(View.OnClickListener onClickListener) {
        btnConfirm.setOnClickListener(onClickListener);
    }

    public void addActionForBtnReSentOtp(View.OnClickListener onClickListener) {
        tvOtpNotReceive.setOnClickListener(onClickListener);
    }
}
