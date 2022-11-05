package com.hisu.zola.fragments.conversation;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ViewFriendAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.databinding.FragmentViewGroupMemberBinding;
import com.hisu.zola.listeners.IOnRemoveUserListener;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;

import java.io.Serializable;
import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewGroupMemberFragment extends Fragment {

    public static final String MEMBER_ARGS = "MEMBER_ARGS";
    private FragmentViewGroupMemberBinding mBinding;
    private MainActivity mainActivity;
    private Conversation conversation;
    private ConversationRepository repository;
    private ViewFriendAdapter adapter;
    private Socket mSocket;
    private LoadingDialog loadingDialog;

    public static ViewGroupMemberFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(MEMBER_ARGS, conversation);
        ViewGroupMemberFragment fragment = new ViewGroupMemberFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(MEMBER_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentViewGroupMemberBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        repository = new ConversationRepository(mainActivity.getApplication());
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        backToPrevPage();
        addActionForBtnAddMember();
        initRecyclerView();
    }

    private void initRecyclerView() {

        adapter = new ViewFriendAdapter(mainActivity);

        repository.getConversationInfo(conversation.getId()).observe(mainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                if(conversation == null) return;

                adapter.setAdmin(conversation.getCreatedBy());
                adapter.setMembers(conversation.getMember());

                if(LocalDataManager.getCurrentUserInfo().getId().equalsIgnoreCase(conversation.getCreatedBy().getId())) {
                    adapter.setOnRemoveUserListener(user -> {
                        new AlertDialog.Builder(mainActivity)
                                .setMessage(getString(R.string.confirm_remove_member))
                                .setNegativeButton(getString(R.string.no), null)
                                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                    removeMember(user.getId());
                                }).show();
                    });

                    adapter.setAdmin(true);
                }

            }
        });

        mBinding.rvMembers.setAdapter(adapter);
        mBinding.rvMembers.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void removeMember(String memberID) {

        loadingDialog.showDialog();

        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        object.addProperty("deleteMemberId", memberID);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        ApiService.apiService.removeMemberFromGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if(response.isSuccessful() && response.code() == 200) {
                    List<User> member = conversation.getMember();
                    for (User user : member) {
                        if(user.getId().equalsIgnoreCase(memberID)) {
                            member.remove(user);
                            break;
                        }
                    }

                    conversation.setMember(member);
                    adapter.setMembers(member);
                    repository.insertOrUpdate(conversation);
                    emitRemoveMember(memberID);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ViewGroupMemberFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitRemoveMember(String memberID) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        loadingDialog.dismissDialog();

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.add("deleteUser", gson.toJsonTree(memberID));

        mSocket.emit("deleteMemberGroup", emitMsg);
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnAddMember() {
        mBinding.iBtnAddMember.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(AddMemberToGroupFragment.newInstance(conversation));
        });
    }
}