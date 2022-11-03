package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.databinding.LayoutChangeGroupNameDialogBinding;

public class ChangeGroupNameDialog {

    private Dialog dialog;
    private final Context context;
    private final int gravity;
    private final Conversation conversation;
    private LayoutChangeGroupNameDialogBinding binding;

    public ChangeGroupNameDialog(Context context, int gravity, Conversation conversation) {
        this.context = context;
        this.gravity = gravity;
        this.conversation = conversation;
        initDialog();
    }

    private void initDialog() {

        binding = LayoutChangeGroupNameDialogBinding.inflate(LayoutInflater.from(context), null, false);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        binding.edtGroupName.setHint(conversation.getLabel());
        binding.edtGroupName.requestFocus();
        addActionForBtnCancel();
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    private void addActionForBtnCancel() {
        binding.tvChange.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    public String getGroupName() {
        return binding.edtGroupName.getText().toString().trim();
    }

    public void addActionForBtnSave(View.OnClickListener onClickListener) {
        binding.tvConfirm.setOnClickListener(onClickListener);
    }
}