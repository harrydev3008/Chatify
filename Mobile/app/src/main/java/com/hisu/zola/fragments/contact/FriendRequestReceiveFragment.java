package com.hisu.zola.fragments.contact;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendRequestReceiveAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentFriendRequestReceiveBinding;
import com.hisu.zola.fragments.conversation.AddNewGroupFragment;
import com.hisu.zola.util.socket.SocketIOHandler;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRequestReceiveFragment extends Fragment {

    private FragmentFriendRequestReceiveBinding mBinding;
    private MainActivity mainActivity;
    private FriendRequestReceiveAdapter adapter;
    private UserRepository userRepository;
    private Socket mSocket;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendRequestReceiveBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSocket = SocketIOHandler.getInstance().getSocketConnection();

        userRepository = new UserRepository(mainActivity.getApplication());
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        adapter = new FriendRequestReceiveAdapter(mainActivity);

        adapter.setAcceptClickListener(this::acceptFriendQuest);

        adapter.setDenyClickListener(this::denyFriendQuest);

        User curUser = LocalDataManager.getCurrentUserInfo();
        userRepository.getUser(curUser.getId()).observe(mainActivity, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) return;

                adapter.setRequestList(user.getFriendsQueue());

                mBinding.rvFriendRequestReceive.setAdapter(adapter);

                mBinding.rvFriendRequestReceive.setLayoutManager(
                        new LinearLayoutManager(
                                mainActivity, LinearLayoutManager.VERTICAL, false
                        )
                );

                getReceiveRequests();
            }
        });
    }

    private void getReceiveRequests() {
        if (adapter.getItemCount() != 0)
            mBinding.imvNoRequestReceive.setVisibility(View.GONE);
        else
            mBinding.imvNoRequestReceive.setVisibility(View.VISIBLE);
    }

    private void acceptFriendQuest(User friend) {
        if (NetworkUtil.isConnectionAvailable(mainActivity)) {
            loadingDialog.showDialog();

            JsonObject object = new JsonObject();
            object.addProperty("acceptFriendId", friend.getId());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

            accept(body, friend.getId());

        } else
            new iOSDialogBuilder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.no_network_connection))
                    .setSubtitle(mainActivity.getString(R.string.no_network_connection))
                    .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
    }

    private void denyFriendQuest(User friend) {
        if (NetworkUtil.isConnectionAvailable(mainActivity)) {
            loadingDialog.showDialog();

            JsonObject object = new JsonObject();
            object.addProperty("deniedFriendId", friend.getId());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

            deny(body);

        } else
            new iOSDialogBuilder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.no_network_connection))
                    .setSubtitle(mainActivity.getString(R.string.no_network_connection))
                    .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
    }

    private void accept(RequestBody body, String acceptID) {
        ApiService.apiService.acceptFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User updatedUser = response.body();
                    userRepository.update(updatedUser);

                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.accept_friend_request_success))
                                .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                        checkConversationExisted(LocalDataManager.getCurrentUserInfo().getId(), acceptID);
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

    private void deny(RequestBody body) {
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

    private void checkConversationExisted(String currentUserID, String acceptID) {
        JsonObject object = new JsonObject();
        List<String> member = List.of(currentUserID, acceptID);

        object.add("member", new Gson().toJsonTree(member));
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.checkGroupExist(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Gson gson = new Gson();

                    String json = gson.toJson(response.body());
                    JsonObject obj = gson.fromJson(json, JsonObject.class);
                    boolean already = obj.get("already").getAsBoolean();

                    if (!already) {
                        addNewGroup(acceptID);
                    }

                    emitAcceptRequest(LocalDataManager.getCurrentUserInfo(), acceptID);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(FriendRequestReceiveFragment.class.getName(), t.getLocalizedMessage());
            }
        });

    }

    private void addNewGroup(String acceptID) {

        User currentUser = LocalDataManager.getCurrentUserInfo();
        Gson gson = new Gson();
        JsonObject object = new JsonObject();

        List<String> members = new ArrayList<>();
        members.add(acceptID);
        members.add(currentUser.getId());

        object.add("member", gson.toJsonTree(members));
        object.add("createdBy", gson.toJsonTree(currentUser));
        object.addProperty("isGroup", false);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        ApiService.apiService.createConversation(body).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Conversation conversation = response.body();
                    emitCreateGroup(conversation);
                    Log.e("test", "here");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(mainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(AddNewGroupFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitCreateGroup(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));

        mSocket.emit("addConversation", emitMsg);
    }

    private void emitAcceptRequest(User sender, String unsentUser) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser);
        mSocket.emit(Constraints.EVT_ACCEPT_FRIEND_REQUEST, object);
    }
}