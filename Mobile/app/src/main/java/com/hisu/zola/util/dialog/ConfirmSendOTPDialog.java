package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutSendOtpDialogBinding;

public class ConfirmSendOTPDialog {

    private Dialog dialog;
    private final Context context;
    private final int gravity;
    private final String dialogDesc;
    private LayoutSendOtpDialogBinding binding;

    public ConfirmSendOTPDialog(Context context, int gravity, String dialogDesc) {
        this.context = context;
        this.gravity = gravity;
        this.dialogDesc = dialogDesc;
        initDialog();
    }

    public void setNewPhoneNumber(String newPhoneNumber) {
        binding.tvNewPhoneNo.setText(newPhoneNumber);
    }

    public String getNewPhoneNumber() {
        return binding.tvNewPhoneNo.getText().toString();
    }

    public Dialog getDialog() {
        return dialog;
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = LayoutSendOtpDialogBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        Window window = dialog.getWindow();

        if (window == null)
            return;

        initUIComponent();

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
    }

    private void initUIComponent() {
        binding.tvDialogDesc.setText(dialogDesc);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void addActionForBtnChange(View.OnClickListener onClickListener) {
        binding.tvChange.setOnClickListener(onClickListener);
    }

    public void addActionForBtnConfirm(View.OnClickListener onClickListener) {
        binding.tvConfirm.setOnClickListener(onClickListener);
    }
}