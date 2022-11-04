package com.hisu.zola.fragments.conversation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.AddGroupMemberAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentAddNewGroupBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewGroupFragment extends Fragment {

    private FragmentAddNewGroupBinding mBinding;
    private MainActivity mainActivity;
    private List<String> members;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAddNewGroupBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.setBottomNavVisibility(View.GONE);
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        init();
        addActionForBtnCancel();
        addActionForBtnDone();
    }

    private void init() {

        members = new ArrayList<>();

        User user = LocalDataManager.getCurrentUserInfo();
        AddGroupMemberAdapter adapter = new AddGroupMemberAdapter(
                user.getFriends(), mainActivity
        );

        adapter.setOnItemCheckedChangListener((friend, isCheck) -> {
            if (isCheck)
                members.add(friend.getId());
            else
                members.remove(friend.getId());

            if (members.size() > 1)
                mBinding.iBtnDone.setVisibility(View.VISIBLE);
            else
                mBinding.iBtnDone.setVisibility(View.GONE);
        });

        mBinding.rvFriends.setAdapter(adapter);
        mBinding.rvFriends.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void addActionForBtnCancel() {
        mBinding.iBtnCancel.setOnClickListener(view -> {
            if (!isDataChanged()) {
                backToPrevPage();
            } else {
                new AlertDialog.Builder(mainActivity)
                        .setMessage(getString(R.string.changes_not_save))
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> backToPrevPage())
                        .setNegativeButton(getString(R.string.no), null).show();
            }
        });
    }

    private void addActionForBtnDone() {
        mBinding.iBtnDone.setOnClickListener(view -> {
            if (validateGroupInfo()) {
                addNewGroup();
            }
        });
    }

    private void backToPrevPage() {
        mainActivity.setBottomNavVisibility(View.VISIBLE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    private boolean isDataChanged() {
        return members.size() > 1;
    }

    private boolean validateGroupInfo() {
        if (mBinding.edtGroupName.getText().toString().trim().isEmpty()) {
            mBinding.edtGroupName.setError(getString(R.string.empty_group_name_err));
            mBinding.edtGroupName.requestFocus();
            return false;
        }

        return true;
    }

    private void addNewGroup() {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("label", mBinding.edtGroupName.getText().toString().trim());
        members.add(currentUser.getId());
        object.add("member", gson.toJsonTree(members));
        object.add("createdBy", gson.toJsonTree(currentUser));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        ApiService.apiService.createConversation(body).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    Conversation conversation = response.body();

                    new AlertDialog.Builder(mainActivity)
                            .setMessage(getString(R.string.add_new_group_success))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                                emitRemoveMember(conversation);
                                mainActivity.setBottomNavVisibility(View.VISIBLE);
                                mainActivity.getSupportFragmentManager().popBackStackImmediate();
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                Log.e(AddNewGroupFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitRemoveMember(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));

        mSocket.emit("addConversation", emitMsg);
    }
}