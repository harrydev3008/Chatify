package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hisu.zola.R;
import com.hisu.zola.databinding.LayoutAddFriendBinding;
import com.hisu.zola.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

public class AddFriendDialog {

    private Dialog dialog;
    private final Context context;
    private int gravity;
    private User findUser;
    private LayoutAddFriendBinding binding;

    public AddFriendDialog(Context context, int gravity, User findUser) {
        this.context = context;
        this.gravity = gravity;
        this.findUser = findUser;
        initDialog();
    }

    private void initDialog() {

        binding = LayoutAddFriendBinding.inflate(LayoutInflater.from(context), null, false);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
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

        binding.tvFriendName.setText(findUser.getUsername());
        binding.tvGender.setText(findUser.isGender() ? context.getString(R.string.gender_m) : context.getString(R.string.gender_f));
//        binding.tvDob.setText(findUser.getDob());
        Glide.with(context).load(findUser.getAvatarURL()).into(binding.cimvFriendPfp);

        User currentUser = LocalDataManager.getCurrentUserInfo();

        for (String friend : currentUser.getFriends())
            if (findUser.getId().equalsIgnoreCase(friend)) {
                binding.btnSentRequest.setVisibility(View.GONE);
                break;
            }

        addActionForBtnDismiss();
        addActionForBtnAddFriend();
    }

    private void addActionForBtnDismiss() {
        binding.iBtnDismiss.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    private void addActionForBtnAddFriend() {
        //Todo: call api to add friend
        binding.btnSentRequest.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}