package com.hisu.zola.util.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.JsonObject;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.LayoutAddFriendBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.local.LocalDataManager;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendDialog {

    private Dialog dialog;
    private final Context context;
    private final int gravity;
    private final User findUser;
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

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
    }

    private void initUIComponent() {

        binding.tvFriendName.setText(findUser.getUsername());
        binding.tvGender.setText(findUser.isGender() ? context.getString(R.string.gender_m) : context.getString(R.string.gender_f));
        binding.tvDob.setText(findUser.getDob());
        Glide.with(context).load(findUser.getAvatarURL()).into(binding.cimvFriendPfp);

        User currentUser = LocalDataManager.getCurrentUserInfo();

        for (User friend : currentUser.getFriends())
            if (findUser.getId().equalsIgnoreCase(friend.getId())) {
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
        binding.btnSentRequest.setOnClickListener(view -> {

            JsonObject object = new JsonObject();
            object.addProperty("userId", findUser.getId());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

            ApiService.apiService.sendFriendRequest(body).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        new iOSDialogBuilder(context)
                                .setTitle(context.getString(R.string.notification_warning))
                                .setTitle(context.getString(R.string.friend_request_sent_success))
                                .setCancelable(false)
                                .setPositiveListener(context.getString(R.string.confirm), iOSDialog::dismiss)
                                .build().show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                    Log.e(AddFriendDialog.class.getName(), t.getLocalizedMessage());
                }
            });

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