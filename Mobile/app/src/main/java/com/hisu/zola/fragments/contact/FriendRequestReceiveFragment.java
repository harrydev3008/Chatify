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
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendRequestReceiveAdapter;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentFriendRequestReceiveBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.NetworkUtil;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;

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

            accept(body);

        } else
            new iOSDialogBuilder(mainActivity)
                    .setTitle(getString(R.string.no_network_connection))
                    .setSubtitle(getString(R.string.no_network_connection))
                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
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
                    .setTitle(getString(R.string.no_network_connection))
                    .setSubtitle(getString(R.string.no_network_connection))
                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
    }

    private void accept(RequestBody body) {
        ApiService.apiService.acceptFriendRequest(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User updatedUser = response.body();
                    userRepository.update(updatedUser);

                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.accept_friend_request_success))
                                .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(getString(R.string.notification_warning))
                            .setSubtitle(getString(R.string.notification_warning_msg))
                            .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
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
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.deny_friend_request_success))
                                .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(getString(R.string.notification_warning))
                            .setSubtitle(getString(R.string.notification_warning_msg))
                            .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(FriendRequestReceiveFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }
}