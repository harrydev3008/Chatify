package com.hisu.zola.fragments.conversation;

import android.app.AlertDialog;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.databinding.FragmentConversationGroupDetailBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.dialog.ChangeGroupNameDialog;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationGroupDetailFragment extends Fragment {

    public static final String GROUP_ARGS = "GROUP_DETAIL";
    private FragmentConversationGroupDetailBinding mBinding;
    private MainActivity mainActivity;
    private Conversation conversation;
    private User currentUser;
    private ConversationRepository repository;
    private ChangeGroupNameDialog groupNameDialog;
    private Socket mSocket;

    public static ConversationGroupDetailFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(GROUP_ARGS, conversation);
        ConversationGroupDetailFragment fragment = new ConversationGroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        currentUser = LocalDataManager.getCurrentUserInfo();

        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(GROUP_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationGroupDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new ConversationRepository(mainActivity.getApplication());
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        loadConversationInfo();
        backToPrevPage();
        addActionForBtnViewMember();
        addActionForBtnOutGroup();
        changeGroupName();
    }

    private void loadConversationInfo() {
        repository.getConversationInfo(conversation.getId()).observe(mainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {

                if (conversation == null) return;

                mBinding.imvGroupPfp.setImageBitmap(ImageConvertUtil.createImageFromText(mainActivity, 150, 150, conversation.getLabel()));
                mBinding.tvGroupName.setText(conversation.getLabel());

                if (conversation.getCreatedBy().getId().equalsIgnoreCase(currentUser.getId())) {
                    mBinding.tvAddMembers.setVisibility(View.VISIBLE);
                    mBinding.tvDisbandGroup.setVisibility(View.VISIBLE);
                    mBinding.tvChangeAdmin.setVisibility(View.VISIBLE);
                    addActionForBtnAddMember();
                    addActionForBtnDisbandGroup();
                }
            }
        });
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.GONE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnAddMember() {
        mBinding.tvAddMembers.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(AddMemberToGroupFragment.newInstance(conversation));
        });
    }

    private void addActionForBtnViewMember() {
        mBinding.tvMembers.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(ViewGroupMemberFragment.newInstance(conversation));
        });
    }

    private void changeGroupName() {
        mBinding.tvGroupName.setOnClickListener(view -> {
            if (groupNameDialog == null)
                groupNameDialog = new ChangeGroupNameDialog(mainActivity, Gravity.CENTER, conversation);

            groupNameDialog.showDialog();
            groupNameDialog.addActionForBtnSave(viewGroup -> {
                changeGroupLabel(groupNameDialog.getGroupName());
            });
        });
    }

    private void changeGroupLabel(String label) {
        JsonObject object = new JsonObject();
        object.addProperty("newLabel", label);
        object.addProperty("conversationId", conversation.getId());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        ApiService.apiService.changeGroupName(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    conversation.setLabel(label);
                    repository.changeGroupName(conversation);
                    groupNameDialog.dismissDialog();
                    emitChangeLabel();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void addActionForBtnDisbandGroup() {
        mBinding.tvDisbandGroup.setOnClickListener(view -> {
            new AlertDialog.Builder(mainActivity)
                    .setMessage(getString(R.string.disband_group_confirm))
                    .setNegativeButton(getString(R.string.no), null)
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        disbandGroup();
                    }).show();
        });
    }

    private void disbandGroup() {
        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        ApiService.apiService.disbandGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    new AlertDialog.Builder(mainActivity)
                            .setMessage(getString(R.string.disband_group_success))
                            .setCancelable(false)
                            .setPositiveButton("ok", (dialogInterface, i) -> {
                                repository.delete(conversation.getId());
                                mainActivity.setBottomNavVisibility(View.GONE);
                                mainActivity.getSupportFragmentManager().popBackStackImmediate();
                                mainActivity.getSupportFragmentManager().popBackStackImmediate();
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void addActionForBtnOutGroup() {
        mBinding.tvOutGroup.setOnClickListener(view -> {
            new AlertDialog.Builder(mainActivity)
                    .setMessage(getString(R.string.confirm_out_group))
                    .setNegativeButton(getString(R.string.no), null)
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        outGroup();
                    }).show();
        });
    }

    //todo: if admin then switch admin role to someone else
    private void outGroup() {
        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        ApiService.apiService.outGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    List<User> members = conversation.getMember();
                    for (User member : members) {
                        if (member.getId().equalsIgnoreCase(currentUser.getId())) {
                            members.remove(member);
                            break;
                        }
                    }

                    conversation.setMember(members);
                    repository.insertOrUpdate(conversation);
                    emitOutGroup();

                    mainActivity.setBottomNavVisibility(View.VISIBLE);
                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitChangeLabel() {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.addProperty("userChange", LocalDataManager.getCurrentUserInfo().getId());

        mSocket.emit("changeGroupName", emitMsg);
    }

    private void emitOutGroup() {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
//        emitMsg.addProperty("userChange", LocalDataManager.getCurrentUserInfo().getId());

        mSocket.emit("outGroup", emitMsg);
    }
}