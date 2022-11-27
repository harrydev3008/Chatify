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

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.databinding.FragmentConversationGroupDetailBinding;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.dialog.ChangeGroupNameDialog;
import com.hisu.zola.util.dialog.HisuIOSDialog;
import com.hisu.zola.util.dialog.HisuIOSDialogBuilder;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.socket.MessageSocketHandler;
import com.hisu.zola.util.socket.SocketIOHandler;

import java.util.ArrayList;
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
    private MessageRepository messageRepository;
    private ChangeGroupNameDialog groupNameDialog;
    private Socket mSocket;
    private LoadingDialog loadingDialog;

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
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        repository = new ConversationRepository(mainActivity.getApplication());
        messageRepository = new MessageRepository(mainActivity.getApplication());
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        loadConversationInfo();
        backToPrevPage();
        addActionForBtnViewMember();
        addActionForBtnOutGroup();
        changeGroupName();
        addActionForBtnViewSentFiles();
    }

    private void loadConversationInfo() {
        repository.getConversationInfo(conversation.getId()).observe(mainActivity, new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversationDB) {

                if (conversationDB == null) return;

                conversation = conversationDB;

                mBinding.imvGroupPfp.setImageBitmap(ImageConvertUtil.createImageFromText(mainActivity, 150, 150, conversationDB.getLabel()));
                mBinding.tvGroupName.setText(conversationDB.getLabel());

                if (conversationDB.getCreatedBy().getId().equalsIgnoreCase(currentUser.getId())) {
                    mBinding.tvAddMembers.setVisibility(View.VISIBLE);
                    mBinding.tvDisbandGroup.setVisibility(View.VISIBLE);
                    mBinding.tvChangeAdmin.setVisibility(View.VISIBLE);
                    addActionForBtnAddMember();
                    addActionForBtnDisbandGroup();
                    addActionForBtnChangeAdmin();
                }
            }
        });
    }

    private void addActionForBtnViewSentFiles() {
        mBinding.tvSentFile.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(SentFilesFragment.newInstance(conversation));
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
        loadingDialog.showDialog();

        JsonObject object = new JsonObject();
        object.addProperty("newLabel", label);
        object.addProperty("conversationId", conversation.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.changeGroupName(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    loadingDialog.dismissDialog();

                    conversation.setLabel(label);
                    repository.changeGroupName(conversation);
                    groupNameDialog.dismissDialog();
                    String holder = currentUser.getUsername() + " vừa đổi tên nhóm thành " + label;
                    sendMessageViaApi(conversation, holder, false);
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
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void addActionForBtnDisbandGroup() {
        mBinding.tvDisbandGroup.setOnClickListener(view -> {
            new iOSDialogBuilder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.confirm))
                    .setSubtitle(mainActivity.getString(R.string.disband_group_confirm))
                    .setPositiveListener(mainActivity.getString(R.string.yes), dialog -> {
                        dialog.dismiss();
                        disbandGroup();
                    })
                    .setNegativeListener(mainActivity.getString(R.string.no), iOSDialog::dismiss).build().show();
        });
    }

    private void disbandGroup() {
        loadingDialog.showDialog();

        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversation.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.disbandGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(mainActivity.getString(R.string.notification_warning))
                                .setSubtitle(mainActivity.getString(R.string.disband_group_success))
                                .setCancelable(false)
                                .setPositiveListener(mainActivity.getString(R.string.confirm), dialog -> {
                                    dialog.dismiss();
                                    emitDisbandGroup(conversation);
                                }).build().show();
                    });
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
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void addActionForBtnChangeAdmin() {
        mBinding.tvChangeAdmin.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(ChangeAdminFragment.newInstance(conversation, ChangeAdminFragment.CHANGE_ADMIN_OPTION_CHANGE_ARGS));
        });
    }

    private void addActionForBtnOutGroup() {
        mBinding.tvOutGroup.setOnClickListener(view -> {
            if (conversation.getCreatedBy().getId().equalsIgnoreCase(currentUser.getId())) {
                new HisuIOSDialogBuilder(mainActivity)
                        .setGravity(Gravity.CENTER)
                        .setCancelListener(HisuIOSDialog::dismiss)
                        .setNegativeListener(dialog -> {
                            dialog.dismiss();
                            for (User user : conversation.getMember()) {
                                if (!user.getId().equalsIgnoreCase(currentUser.getId())) {
                                    changeAdmin(user);
                                    break;
                                }
                            }
                        })
                        .setPositiveListener(dialog -> {
                            dialog.dismiss();
                            mainActivity.addFragmentToBackStack(ChangeAdminFragment.newInstance(
                                    conversation, ChangeAdminFragment.CHANGE_ADMIN_OPTION_DELETE_ARGS)
                            );
                        })
                        .build().show();
            } else {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.confirm))
                        .setSubtitle(mainActivity.getString(R.string.confirm_out_group))
                        .setPositiveListener(mainActivity.getString(R.string.yes), dialog -> {
                            dialog.dismiss();
                            String holder = currentUser.getUsername() + " vừa rời khỏi nhóm.";
                            sendMessageViaApi(conversation, holder, true);
                        })
                        .setNegativeListener(mainActivity.getString(R.string.no), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void outGroup(Conversation conversationEmit) {
        loadingDialog.showDialog();

        JsonObject object = new JsonObject();
        object.addProperty("conversationId", conversationEmit.getId());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.outGroup(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    List<User> members = conversationEmit.getMember();
                    for (User member : members) {
                        if (member.getId().equalsIgnoreCase(currentUser.getId())) {
                            members.remove(member);
                            break;
                        }
                    }

                    conversationEmit.setMember(members);
                    emitOutGroup(conversationEmit);
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
                Log.e(ConversationGroupDetailFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void emitChangeLabel() {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        loadingDialog.dismissDialog();

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.addProperty("userChange", LocalDataManager.getCurrentUserInfo().getId());

        mSocket.emit(Constraints.EVT_CHANGE_GROUP_NAME, emitMsg);
    }

    private void emitOutGroup(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        loadingDialog.dismissDialog();

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));

        repository.delete(conversation.getId());
        mSocket.emit(Constraints.EVT_OUT_GROUP, emitMsg);

        mainActivity.getSupportFragmentManager().popBackStackImmediate();
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
        mainActivity.setBottomNavVisibility(View.VISIBLE);
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
                    emitChangeAdmin(conversation);
                    String holder = currentUser.getUsername() + " vừa rời nhóm và chọn " + newAdmin.getUsername() + " làm trưởng nhóm mới.";
                    sendMessageViaApi(conversation, holder, true);
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
                Log.e(ChangeAdminFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void sendMessageViaApi(Conversation conversation, String text, boolean outGroup) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();

        object.add("conversation", gson.toJsonTree(conversation));
        object.addProperty("sender", LocalDataManager.getCurrentUserInfo().getId());
        object.addProperty("text", text);
        object.addProperty("type", "notification");
        object.add("media", gson.toJsonTree(new ArrayList<>()));

        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.sendMessage(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    String json = gson.toJson(response.body());

                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    Message message = gson.fromJson(obj.get("data"), Message.class);
                    sendMessage(conversation, message, outGroup);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(MessageSocketHandler.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void sendMessage(Conversation conversation, Message message, boolean outGroup) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.add("sender", gson.toJsonTree(LocalDataManager.getCurrentUserInfo()));

        emitMsg.addProperty("text", message.getText());
        emitMsg.addProperty("type", message.getType());
        emitMsg.add("media", gson.toJsonTree(message.getMedia()));
        emitMsg.addProperty("isDelete", false);
        emitMsg.addProperty("_id", message.getId());
        emitMsg.addProperty("createdAt", message.getCreatedAt());
        emitMsg.addProperty("updatedAt", message.getUpdatedAt());

        mSocket.emit(Constraints.EVT_MESSAGE_SEND, emitMsg);

        if (outGroup)
            outGroup(conversation);
        else {
            messageRepository.insertOrUpdate(message);
            emitChangeLabel();
        }
    }

    private void emitChangeAdmin(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        loadingDialog.dismissDialog();

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));

        mSocket.emit(Constraints.EVT_CHANGE_GROUP_ADMIN, emitMsg);
    }

    private void emitDisbandGroup(Conversation conversation) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        loadingDialog.dismissDialog();

        Gson gson = new Gson();

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));

        mSocket.emit(Constraints.EVT_DELETE_GROUP, emitMsg);

        repository.delete(conversation.getId());
        mainActivity.setBottomNavVisibility(View.VISIBLE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }
}