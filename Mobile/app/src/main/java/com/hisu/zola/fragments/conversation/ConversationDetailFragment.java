package com.hisu.zola.fragments.conversation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentConversationDetailBinding;
import com.hisu.zola.fragments.contact.FriendRequestReceiveFragment;
import com.hisu.zola.fragments.contact.FriendRequestSendFragment;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.dialog.LoadingDialog;
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

public class ConversationDetailFragment extends Fragment {

    public static final String USER_ARGS = "USER_DETAIL";
    public static final String CONVERSATION_ARGS = "CONVERSATION_ARGS";
    private FragmentConversationDetailBinding mBinding;
    private MainActivity mainActivity;
    private User user;
    private Conversation conversation;
    private UserRepository userRepository;
    private Socket mSocket;
    private LoadingDialog loadingDialog;

    public static ConversationDetailFragment newInstance(User user, Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(USER_ARGS, user);
        args.putSerializable(CONVERSATION_ARGS, conversation);
        ConversationDetailFragment fragment = new ConversationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER_ARGS);
            conversation = (Conversation) getArguments().getSerializable(CONVERSATION_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        userRepository = new UserRepository(mainActivity.getApplication());

        loadUserDetail(user);
        addActionForBackBtn();
        addActionForEventViewSentFiles();
        addActionForEventUnfriend();
        addActionForEventAddFriend();
        addActionForEventUnsentRequest();
        addActionForEventDenyFriendRequest();
    }

    private void loadUserDetail(User user) {
        mBinding.tvFriendName.setText(user.getUsername());

        if (user.getAvatarURL() == null || user.getAvatarURL().isEmpty())
            mBinding.imvFriendPfp.setImageBitmap(ImageConvertUtil.createImageFromText(mainActivity, 150, 150, user.getUsername()));
        else
            Glide.with(mainActivity).asBitmap()
                    .load(user.getAvatarURL())
                    .placeholder(AppCompatResources.getDrawable(mainActivity, R.drawable.ic_img_place_holder))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mBinding.imvFriendPfp.setImageBitmap(resource);
                            mBinding.imvFriendPfp.setVisibility(View.VISIBLE);
                        }
                    });

        userRepository.getUser(LocalDataManager.getCurrentUserInfo().getId()).observe(mainActivity, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) return;

                LocalDataManager.setCurrentUserInfo(user);
                checkFriendInfo();
            }
        });
    }

    private void addActionForBackBtn() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForEventViewSentFiles() {
        mBinding.tvSentFile.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(SentFilesFragment.newInstance(conversation));
        });
    }

    private void addActionForEventUnsentRequest() {
        mBinding.tvUnsentRequest.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mainActivity))
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.notification_warning))
                        .setCancelable(false)
                        .setSubtitle(mainActivity.getString(R.string.friend_request_unsent_confirm))
                        .setNegativeListener(mainActivity.getString(R.string.no), iOSDialog::dismiss)
                        .setPositiveListener(mainActivity.getString(R.string.yes), dialog -> {
                            dialog.dismiss();
                            unSentFriendRequest(user);
                        }).build().show();
            else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(getString(R.string.no_network_connection))
                        .setSubtitle(getString(R.string.no_network_connection_desc))
                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void addActionForEventUnfriend() {
        mBinding.tvUnfriend.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mainActivity))
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.confirm))
                        .setSubtitle(mainActivity.getString(R.string.unfriend_confirm))
                        .setCancelable(false)
                        .setNegativeListener(mainActivity.getString(R.string.no), iOSDialog::dismiss)
                        .setPositiveListener(mainActivity.getString(R.string.yes), dialog -> {
                            dialog.dismiss();
                            unfriend();
                        }).build().show();
            else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(getString(R.string.no_network_connection))
                        .setSubtitle(getString(R.string.no_network_connection_desc))
                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void addActionForEventAddFriend() {
        mBinding.tvAddFriend.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mainActivity))
                addFriend(user.getId());
            else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(getString(R.string.no_network_connection))
                        .setSubtitle(getString(R.string.no_network_connection_desc))
                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void addActionForEventDenyFriendRequest() {
        mBinding.tvDenyRequest.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mainActivity))
                denyFriendRequest(user.getId());
            else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(getString(R.string.no_network_connection))
                        .setSubtitle(getString(R.string.no_network_connection_desc))
                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void checkFriendInfo() {
        boolean isFriend = isFriend();
        boolean isSendRequest = isSendRequest();
        boolean isReceive = isReceiveRequest();
        if (isFriend) {
            mBinding.tvUnfriend.setVisibility(View.VISIBLE);
            mBinding.tvAddFriend.setVisibility(View.GONE);
            mBinding.tvUnsentRequest.setVisibility(View.GONE);
            mBinding.tvDenyRequest.setVisibility(View.GONE);
        } else if (isSendRequest) {
            mBinding.tvUnfriend.setVisibility(View.GONE);
            mBinding.tvAddFriend.setVisibility(View.GONE);
            mBinding.tvUnsentRequest.setVisibility(View.VISIBLE);
            mBinding.tvDenyRequest.setVisibility(View.GONE);
        } else if (isReceive) {
            mBinding.tvUnfriend.setVisibility(View.GONE);
            mBinding.tvAddFriend.setVisibility(View.GONE);
            mBinding.tvUnsentRequest.setVisibility(View.GONE);
            mBinding.tvDenyRequest.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvUnfriend.setVisibility(View.GONE);
            mBinding.tvAddFriend.setVisibility(View.VISIBLE);
            mBinding.tvUnsentRequest.setVisibility(View.GONE);
            mBinding.tvDenyRequest.setVisibility(View.GONE);
        }
    }

    private boolean isFriend() {
        List<User> friends = LocalDataManager.getCurrentUserInfo().getFriends();

        for (User friend : friends) {
            if (friend.getId().equalsIgnoreCase(user.getId()))
                return true;
        }

        return false;
    }

    private boolean isSendRequest() {
        List<User> friends = LocalDataManager.getCurrentUserInfo().getSendRequestQueue();

        for (User friend : friends) {
            if (friend.getId().equalsIgnoreCase(user.getId()))
                return true;
        }

        return false;
    }

    private boolean isReceiveRequest() {
        List<User> friends = LocalDataManager.getCurrentUserInfo().getFriendsQueue();

        for (User friend : friends) {
            if (friend.getId().equalsIgnoreCase(user.getId()))
                return true;
        }

        return false;
    }

    private void addFriend(String friendID) {
        JsonObject object = new JsonObject();
        object.addProperty("userId", friendID);
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.sendFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    User addReqUser = response.body();
                    userRepository.update(addReqUser);
                    emitAddFriend(addReqUser, friendID);

                    mainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.friend_request_sent_success))
                                .setCancelable(false)
                                .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss)
                                .build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(ConversationDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void unfriend() {
        JsonObject object = new JsonObject();
        object.addProperty("deleteFriendId", user.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.unfriend(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User curUser = response.body();
                    userRepository.update(curUser);
                    emitUnFriend(curUser, user.getId());

                    mainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.unfriend_success))
                                .setCancelable(false)
                                .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(ConversationDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void unSentFriendRequest(User user) {
        JsonObject object = new JsonObject();
        object.addProperty("recallFriendRequestId", user.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.unSendFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    User currentUser = LocalDataManager.getCurrentUserInfo();
                    List<User> sendList = currentUser.getSendRequestQueue();

                    for (User send : sendList) {
                        if (send.getId().equalsIgnoreCase(user.getId())) {
                            sendList.remove(send);
                            break;
                        }
                    }

                    currentUser.setSendRequestQueue(sendList);
                    userRepository.update(currentUser);
                    LocalDataManager.setCurrentUserInfo(currentUser);
                    emitUnSendRequest(currentUser, user);

                    new iOSDialogBuilder(mainActivity)
                            .setTitle(mainActivity.getString(R.string.notification_warning))
                            .setCancelable(false)
                            .setSubtitle(mainActivity.getString(R.string.friend_request_unsent))
                            .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(FriendRequestSendFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void denyFriendRequest(String friendID) {
        JsonObject object = new JsonObject();
        object.addProperty("deniedFriendId", friendID);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        ApiService.apiService.denyFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User updatedUser = response.body();
                    userRepository.update(updatedUser);

                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.deny_friend_request_success))
                                .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(mainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(FriendRequestReceiveFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitAddFriend(User sender, String unsentUser) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser);
        mSocket.emit(Constraints.EVT_ADD_FRIEND, object);
    }

    private void emitUnFriend(User sender, String unsentUser) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser);
        mSocket.emit(Constraints.EVT_DELETE_FRIEND, object);
    }

    private void emitUnSendRequest(User sender, User unsentUser) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser.getId());
        mSocket.emit(Constraints.EVT_UNSENT_FRIEND_REQUEST, object);
    }
}