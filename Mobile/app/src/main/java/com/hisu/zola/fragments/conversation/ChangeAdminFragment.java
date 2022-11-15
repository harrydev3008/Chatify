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
import com.hisu.zola.adapters.ChangeAdminAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.databinding.FragmentChangeAdminBinding;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.Constraints;

import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeAdminFragment extends Fragment {

    public static final String CHANGE_ADMIN_ARGS = "CHANGE_ADMIN_ARGS";
    public static final String CHANGE_ADMIN_OPTION_ARGS = "CHANGE_ADMIN_OPTION_ARGS";
    public static final String CHANGE_ADMIN_OPTION_CHANGE_ARGS = "CHANGE_ADMIN_OPTION_CHANGE_ARGS";
    public static final String CHANGE_ADMIN_OPTION_DELETE_ARGS = "CHANGE_ADMIN_OPTION_DELETE_ARGS";

    private FragmentChangeAdminBinding mBinding;
    private MainActivity mainActivity;
    private ChangeAdminAdapter adapter;
    private ConversationRepository repository;
    private Conversation conversation;
    private Socket mSocket;
    private String option;
    private LoadingDialog loadingDialog;

    public static ChangeAdminFragment newInstance(Conversation conversation, String option) {
        Bundle args = new Bundle();
        args.putSerializable(CHANGE_ADMIN_ARGS, conversation);
        args.putString(CHANGE_ADMIN_OPTION_ARGS, option);
        ChangeAdminFragment fragment = new ChangeAdminFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(CHANGE_ADMIN_ARGS);
            option = getArguments().getString(CHANGE_ADMIN_OPTION_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentChangeAdminBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        repository = new ConversationRepository(mainActivity.getApplication());
        init();

        backToPrevPage();
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void init() {
        adapter = new ChangeAdminAdapter(mainActivity);

        repository.getConversationInfo(conversation.getId()).observe(mainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversationDB) {
                if (conversationDB == null) return;

                conversation = conversationDB;

                adapter.setMembers(conversationDB.getMember());

                if (LocalDataManager.getCurrentUserInfo().getId().equalsIgnoreCase(conversationDB.getCreatedBy().getId())) {
                    adapter.setOnRemoveUserListener(user -> {
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(getString(R.string.confirm))
                                .setSubtitle(getString(R.string.change_admin_confirm))
                                .setPositiveListener(getString(R.string.yes), dialog -> {
                                    dialog.dismiss();
                                    if (option.equalsIgnoreCase(CHANGE_ADMIN_OPTION_CHANGE_ARGS))
                                        changeAdmin(user);
                                    else if (option.equalsIgnoreCase(CHANGE_ADMIN_OPTION_DELETE_ARGS)) {
                                        changeAdmin(user);
                                    }
                                })
                                .setNegativeListener(getString(R.string.no), iOSDialog::dismiss).build().show();
                    });
                }

            }
        });

        mBinding.rvMembers.setAdapter(adapter);
        mBinding.rvMembers.setLayoutManager(new

                LinearLayoutManager(mainActivity));
    }

    private void changeAdmin(User newAdmin) {
        loadingDialog.showDialog();

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        object.add("newCreator", gson.toJsonTree(newAdmin));

        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.changeGroupAdmin(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    conversation.setCreatedBy(newAdmin);
                    repository.insertOrUpdate(conversation);

                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.change_admin_success))
                                .setCancelable(false)
                                .setPositiveListener(getString(R.string.confirm), dialog -> {
                                    dialog.dismiss();
                                    if (option.equalsIgnoreCase(CHANGE_ADMIN_OPTION_CHANGE_ARGS)) {
                                        emitChangeAdmin(conversation);
                                        mainActivity.getSupportFragmentManager().popBackStackImmediate();
                                    } else if (option.equalsIgnoreCase(CHANGE_ADMIN_OPTION_DELETE_ARGS)) {
                                        outGroup();
                                    }
                                })
                                .build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.dismissDialog();
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(getString(R.string.notification_warning))
                            .setSubtitle(getString(R.string.notification_warning_msg))
                            .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                });
                Log.e(ChangeAdminFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void outGroup() {
        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.outGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    emitOutGroup(conversation);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitOutGroup(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        List<User> members = conversation.getMember();
        for (User member : members) {
            if (member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
                members.remove(member);
                break;
            }
        }

        conversation.setMember(members);

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        mSocket.emit("outGroup", emitMsg);
        repository.delete(conversation.getId());

        mainActivity.setBottomNavVisibility(View.VISIBLE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    private void emitChangeAdmin(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));

        mSocket.emit("changeCreatorGroup", emitMsg);
    }
}