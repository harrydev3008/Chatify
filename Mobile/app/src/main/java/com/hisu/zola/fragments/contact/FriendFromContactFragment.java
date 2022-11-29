package com.hisu.zola.fragments.contact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendFromContactAdapter;
import com.hisu.zola.database.Database;
import com.hisu.zola.database.entity.ContactUser;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentFriendFromContactBinding;
import com.hisu.zola.fragments.conversation.ConversationDetailFragment;
import com.hisu.zola.listeners.IOnContractUserClickListener;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;
import com.hisu.zola.util.socket.SocketIOHandler;
import com.hisu.zola.view_model.ContactUserViewModel;
import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendFromContactFragment extends Fragment {

    private FragmentFriendFromContactBinding mBinding;
    private MainActivity mainActivity;
    private FriendFromContactAdapter adapter;
    private ContactUserViewModel viewModel;
    private List<ContactUser> contactUsers;
    private User currentUser;
    private Socket mSocket;
    private LoadingDialog loadingDialog;
    private UserRepository userRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendFromContactBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        userRepository = new UserRepository(mainActivity.getApplication());
        contactUsers = new ArrayList<>();
        currentUser = LocalDataManager.getCurrentUserInfo();
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);

        init();
        backToPrevPage();
        addActionForNotFriendTab();
        addActionForFriendTab();
        addActionForBtnSyncFromContact();
    }

    private void init() {
        adapter = new FriendFromContactAdapter(mainActivity);
        adapter.setOnContractUserClickListener(new IOnContractUserClickListener() {
            @Override
            public void onClick(ContactUser contactUser) {
                addActionForEventAddFriend(contactUser);
            }
        });
        mBinding.rvFriendsFromContact.setAdapter(adapter);

        viewModel = new ViewModelProvider(mainActivity).get(ContactUserViewModel.class);

        viewModel.getData().observe(mainActivity, new Observer<List<ContactUser>>() {
            @Override
            public void onChanged(List<ContactUser> contactUserList) {
                if (contactUserList == null) return;
                contactUsers.clear();
                contactUsers.addAll(contactUserList);
                adapter.setContactUsers(contactUserList);
            }
        });

        mBinding.rvFriendsFromContact.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnSyncFromContact() {
        mBinding.iBtnSyncContact.setOnClickListener(view -> {
            if (isReadContactPermissionGranted()) {
                if (NetworkUtil.isConnectionAvailable(mainActivity))
                    getContacts();
                else
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(mainActivity.getString(R.string.no_network_connection))
                            .setSubtitle(mainActivity.getString(R.string.no_network_connection_to_sync))
                            .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
            } else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.notification_warning))
                        .setSubtitle(mainActivity.getString(R.string.ask_contact_permission))
                        .setNegativeListener(mainActivity.getString(R.string.deny), iOSDialog::dismiss)
                        .setPositiveListener(mainActivity.getString(R.string.allow), dialog -> {
                            dialog.dismiss();
                            requestReadContactPermission();
                        }).build().show();
            }
        });
    }

    private boolean isReadContactPermissionGranted() {
        return ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactPermission() {
        String[] permissions = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(mainActivity, permissions, Constraints.CONTACT_PERMISSION_CODE);
    }

    public void getContacts() {
        Database.dbExecutor.execute(() -> {

            mainActivity.runOnUiThread(() -> {
                loadingDialog.showDialog();
            });

            User currentUserInfo = LocalDataManager.getCurrentUserInfo();

            List<ContactData> contactDataList = new ContactsGetterBuilder(mainActivity)
                    .allFields()
                    .buildList();

            for (ContactData contactData : contactDataList)
                if (contactData.getPhoneList().size() != 0) {
                    String phoneNumber = contactData.getPhoneList().get(0).getMainData().replaceAll("[^0-9]", "");

                    JsonObject object = new JsonObject();
                    object.addProperty("phoneNumber", phoneNumber);
                    RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
                    ApiService.apiService.findFriendByPhoneNumber(body).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                User user = response.body();
                                if (user != null) {
                                    boolean isFriend = isFriend(user);
                                    String avatar = "";

                                    if (isFriend)
                                        avatar = user.getAvatarURL();

                                    if (!user.getId().equalsIgnoreCase(currentUserInfo.getId()))
                                        viewModel.insertOrUpdate(
                                                new ContactUser(user.getId(), contactData.getCompositeName(), user.getUsername(), phoneNumber, avatar, "", true, isFriend)
                                        );
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            Log.e(FriendFromContactAdapter.class.getName(), t.getLocalizedMessage());
                        }
                    });
                }

            mainActivity.runOnUiThread(() -> {
                loadingDialog.dismissDialog();
            });
        });
    }

    private boolean isFriend(User foundUser) {
        for (User friend : currentUser.getFriends()) {
            if (friend.getPhoneNumber().equalsIgnoreCase(foundUser.getPhoneNumber()))
                return true;
        }

        return false;
    }

    private void addActionForNotFriendTab() {
        mBinding.tvNotFriends.setOnClickListener(view -> {
            switchClickState(mBinding.tvNotFriends, mBinding.tvAllFriends);
            List<ContactUser> temp = new ArrayList<>(contactUsers);
            adapter.setContactUsers(temp.stream().filter(contactUser -> !contactUser.isFriend()).collect(Collectors.toList()));
        });
    }

    private void addActionForFriendTab() {
        mBinding.tvAllFriends.setOnClickListener(view -> {
            switchClickState(mBinding.tvAllFriends, mBinding.tvNotFriends);
            adapter.setContactUsers(contactUsers);
        });
    }

    private void switchClickState(TextView active, TextView inActive) {
        inActive.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.btn_outline_rounded));
        inActive.setTextColor(ContextCompat.getColor(mainActivity, R.color.gray));

        active.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.btn_solid_rounded));
        active.setTextColor(ContextCompat.getColor(mainActivity, R.color.black));
    }

    private void addActionForEventAddFriend(ContactUser user) {
        if (NetworkUtil.isConnectionAvailable(mainActivity))
            addFriend(user.get_id());
        else {
            new iOSDialogBuilder(mainActivity)
                    .setTitle(getString(R.string.no_network_connection))
                    .setSubtitle(getString(R.string.no_network_connection_desc))
                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
        }
    }

    private void addFriend(String friendID) {

        mainActivity.runOnUiThread(() -> {
            loadingDialog.showDialog();
        });

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
                        mainActivity.runOnUiThread(() -> {
                            loadingDialog.dismissDialog();
                        });
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
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(mainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mainActivity.getString(R.string.notification_warning_msg))
                            .setCancelable(false)
                            .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(ConversationDetailFragment.class.getName(), t.getLocalizedMessage());
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
}