package com.hisu.zola.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.LayoutAddFriendBinding;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;
import com.hisu.zola.util.socket.SocketIOHandler;

import java.util.List;

import io.socket.client.Socket;
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
    private User currentUser;
    private final UserRepository userRepository;
    private final Socket mSocket;

    public AddFriendDialog(Context context, int gravity, User findUser) {
        this.context = context;
        this.gravity = gravity;
        this.findUser = findUser;
        userRepository = new UserRepository(((MainActivity) context).getApplication());
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        initDialog();
    }

    private void initDialog() {

        binding = LayoutAddFriendBinding.inflate(LayoutInflater.from(context), null, false);
        currentUser = LocalDataManager.getCurrentUserInfo();
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
        Glide.with(context)
                .asBitmap().load(findUser.getAvatarURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.cimvFriendPfp.setImageBitmap(resource);
                        binding.cimvFriendPfp.setVisibility(View.VISIBLE);
                    }
                });

        if (findUser.getId().equalsIgnoreCase(currentUser.getId())) {
            binding.btnSentRequest.setVisibility(View.GONE);
            binding.btnUnFriend.setVisibility(View.GONE);
        } else if (isFriend()) {
            binding.btnSentRequest.setVisibility(View.GONE);
            binding.btnUnFriend.setVisibility(View.VISIBLE);
        } else if (isSendRequest() || isReceiveRequest()) {
            binding.btnSentRequest.setVisibility(View.GONE);
            binding.btnUnFriend.setVisibility(View.GONE);
        } else {
            binding.btnSentRequest.setVisibility(View.VISIBLE);
            binding.btnUnFriend.setVisibility(View.GONE);
        }

        addActionForBtnDismiss();
        addActionForBtnAddFriend();
        addActionForBtnUnfriend();
    }

    private boolean isFriend() {
        List<User> friends = currentUser.getFriends();

        for (User friend : friends)
            if (findUser.getId().equalsIgnoreCase(friend.getId()))
                return true;

        return false;
    }

    private boolean isSendRequest() {
        List<User> friends = currentUser.getSendRequestQueue();

        for (User friend : friends)
            if (findUser.getId().equalsIgnoreCase(friend.getId()))
                return true;

        return false;
    }

    private boolean isReceiveRequest() {
        List<User> friends = currentUser.getFriendsQueue();

        for (User friend : friends)
            if (findUser.getId().equalsIgnoreCase(friend.getId()))
                return true;

        return false;
    }

    private void addActionForBtnDismiss() {
        binding.iBtnDismiss.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    private void addActionForBtnAddFriend() {
        binding.btnSentRequest.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(context)) {
                addFriend();
            } else {
                new iOSDialogBuilder(context)
                        .setTitle(context.getString(R.string.no_network_connection))
                        .setSubtitle(context.getString(R.string.no_network_connection_desc))
                        .setPositiveListener(context.getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void addActionForBtnUnfriend() {
        binding.btnUnFriend.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(context)) {
                new iOSDialogBuilder(context)
                        .setTitle(context.getString(R.string.notification_warning))
                        .setSubtitle(context.getString(R.string.unfriend_confirm))
                        .setCancelable(false)
                        .setPositiveListener(context.getString(R.string.yes), dialog1 -> {
                            dialog1.dismiss();
                            unfriend();
                        })
                        .setNegativeListener(context.getString(R.string.notification_warning), iOSDialog::dismiss)
                        .build().show();
            } else {
                new iOSDialogBuilder(context)
                        .setTitle(context.getString(R.string.no_network_connection))
                        .setSubtitle(context.getString(R.string.no_network_connection_desc))
                        .setPositiveListener(context.getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void addFriend() {
        JsonObject object = new JsonObject();
        object.addProperty("userId", findUser.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.sendFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    User updatedUser = response.body();

                    if (updatedUser != null) {

                        userRepository.update(updatedUser);
                        emitAddFriend(updatedUser, findUser.getId());

                        new iOSDialogBuilder(context)
                                .setTitle(context.getString(R.string.notification_warning))
                                .setSubtitle(context.getString(R.string.friend_request_sent_success))
                                .setCancelable(false)
                                .setPositiveListener(context.getString(R.string.confirm), dialog1 -> {
                                    dialog1.dismiss();
                                    dismissDialog();
                                })
                                .build().show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(AddFriendDialog.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void unfriend() {
        JsonObject object = new JsonObject();
        object.addProperty("deleteFriendId", findUser.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.unfriend(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User curUser = response.body();
                    userRepository.update(curUser);
                    emitUnFriend(curUser, findUser.getId());

                    new iOSDialogBuilder(context)
                            .setTitle(context.getString(R.string.notification_warning))
                            .setSubtitle(context.getString(R.string.unfriend_success))
                            .setCancelable(false)
                            .setPositiveListener(context.getString(R.string.confirm), dialog1 -> {
                                dialog1.dismiss();
                                dismissDialog();
                            }).build().show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(AddFriendDialog.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    private void emitAddFriend(User sender, String unsentUser) {

        if(!mSocket.connected())
            mSocket.connect();

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser);
        mSocket.emit(Constraints.EVT_ADD_FRIEND, object);
    }

    private void emitUnFriend(User sender, String unsentUser) {

        if(!mSocket.connected())
            mSocket.connect();

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser);
        mSocket.emit(Constraints.EVT_DELETE_FRIEND, object);
    }
}