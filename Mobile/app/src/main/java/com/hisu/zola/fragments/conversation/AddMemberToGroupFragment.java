package com.hisu.zola.fragments.conversation;

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
import com.hisu.zola.adapters.AddGroupMemberAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.databinding.FragmentAddMemberToGroupBinding;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;
import com.hisu.zola.util.socket.MessageHandler;
import com.hisu.zola.util.socket.MessageSocketHandler;
import com.hisu.zola.util.socket.SocketIOHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberToGroupFragment extends Fragment {

    public static final String ADD_MEMBER_ARGS = "ADD_MEMBER_ARGS";
    private FragmentAddMemberToGroupBinding mBinding;
    private MainActivity mainActivity;
    private List<String> members;
    private List<User> newMembers;
    private Conversation conversation;
    private ConversationRepository repository;
    private Socket mSocket;
    private LoadingDialog loadingDialog;

    public static AddMemberToGroupFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(ADD_MEMBER_ARGS, conversation);
        AddMemberToGroupFragment fragment = new AddMemberToGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(ADD_MEMBER_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAddMemberToGroupBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        repository = new ConversationRepository(mainActivity.getApplication());
        addActionForBtnCancel();
        init();
        addActionForBtnCancel();
        addActionForBtnDone();
    }

    private void init() {

        members = new ArrayList<>();
        newMembers = new ArrayList<>();

        repository.getConversationInfo(conversation.getId()).observe(mainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation dbConversation) {
                if (dbConversation == null) return;

                conversation = dbConversation;

                AddGroupMemberAdapter adapter = new AddGroupMemberAdapter(
                        getFriendNotInGroup(), mainActivity
                );

                adapter.setOnItemCheckedChangListener((friend, isCheck) -> {
                    if (isCheck) {
                        members.add(friend.getId());
                        newMembers.add(friend);
                    } else {
                        members.remove(friend.getId());
                        newMembers.remove(friend);
                    }

                    if (members.size() > 0)
                        mBinding.iBtnDone.setVisibility(View.VISIBLE);
                    else
                        mBinding.iBtnDone.setVisibility(View.GONE);
                });

                mBinding.rvMembers.setAdapter(adapter);
                mBinding.rvMembers.setLayoutManager(new LinearLayoutManager(mainActivity));
            }
        });
    }

    private List<User> getFriendNotInGroup() {
        List<User> friends = LocalDataManager.getCurrentUserInfo().getFriends();

        Map<String, User> temp = friends.stream().collect(Collectors.toMap(User::getId, user -> user));

        for (User member : conversation.getMember()) {
            if (temp.containsKey(member.getId()))
                temp.remove(member.getId());
        }

        return new ArrayList<>(temp.values());
    }

    private void addActionForBtnCancel() {
        mBinding.iBtnCancel.setOnClickListener(view -> {
            if (!isDataChanged()) {
                backToPrevPage();
            } else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.notification_warning))
                        .setSubtitle(mainActivity.getString(R.string.changes_not_save))
                        .setPositiveListener(mainActivity.getString(R.string.yes), dialog -> {
                            dialog.dismiss();
                            backToPrevPage();
                        })
                        .setNegativeListener(mainActivity.getString(R.string.no), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void backToPrevPage() {
        mainActivity.setBottomNavVisibility(View.GONE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    private void addActionForBtnDone() {
        mBinding.iBtnDone.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mainActivity))
                addMember();
            else
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.no_network_connection))
                        .setSubtitle(mainActivity.getString(R.string.no_network_connection_desc))
                        .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
        });
    }

    private void addMember() {
        loadingDialog.showDialog();

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        object.add("newMember", gson.toJsonTree(members));

        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.addMemberToGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    loadingDialog.dismissDialog();

                    List<User> newGroupMembers = conversation.getMember();
                    newGroupMembers.addAll(newMembers);

                    conversation.setMember(newGroupMembers);
                    repository.insertOrUpdate(conversation);
                    emitAddMember();

                    for (User newMember : newMembers) {
                        MessageHandler.sendMessageViaApi(mainActivity, conversation, getTextFromMember(newMember), false);
                    }

                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(mainActivity.getString(R.string.notification_warning))
                            .setSubtitle(mainActivity.getString(R.string.notification_warning_msg))
                            .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });

                Log.e(AddMemberToGroupFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private String getTextFromMember(User member) {
        return LocalDataManager.getCurrentUserInfo().getUsername() + " vừa thêm " + member.getUsername() + " vào nhóm.";
    }

    private void emitAddMember() {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.addProperty("userChange", LocalDataManager.getCurrentUserInfo().getId());

        mSocket.emit(Constraints.EVT_ADD_MEMBER, emitMsg);
    }

    private boolean isDataChanged() {
        return members.size() > 1;
    }
}