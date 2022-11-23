package com.hisu.zola.fragments.contact;

import android.os.Bundle;
import android.util.Log;
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
import com.hisu.zola.adapters.FriendRequestSendAdapter;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentFriendRequestSendBinding;
import com.hisu.zola.listeners.IOnUserClickListener;
import com.hisu.zola.util.socket.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;

import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRequestSendFragment extends Fragment {

    private FragmentFriendRequestSendBinding mBinding;
    private MainActivity mainActivity;
    private UserRepository userRepository;
    private FriendRequestSendAdapter sendAdapter;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendRequestSendBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        userRepository = new UserRepository(mainActivity.getApplication());

        sendAdapter = new FriendRequestSendAdapter(mainActivity);
        sendAdapter.setOnUserClickListener(new IOnUserClickListener() {
            @Override
            public void onClick(User user) {
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
            }
        });

        mBinding.rvFriendRequestSend.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        User curUser = LocalDataManager.getCurrentUserInfo();
        userRepository.getUser(curUser.getId()).observe(mainActivity, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) return;

                sendAdapter.setRequestList(user.getSendRequestQueue());
                mBinding.rvFriendRequestSend.setAdapter(sendAdapter);

                getSendRequests();
            }
        });
    }

    private void getSendRequests() {
        if (sendAdapter.getItemCount() != 0)
            mBinding.imvNoRequestSend.setVisibility(View.GONE);
        else
            mBinding.imvNoRequestSend.setVisibility(View.VISIBLE);
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

    private void emitUnSendRequest(User sender, User unsentUser) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.add("sender", gson.toJsonTree(sender));
        object.addProperty("recipient", unsentUser.getId());
        mSocket.emit(Constraints.EVT_UNSENT_FRIEND_REQUEST, object);
    }
}