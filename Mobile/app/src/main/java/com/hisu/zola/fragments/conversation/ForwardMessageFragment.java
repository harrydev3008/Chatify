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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ForwardMessageAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.databinding.FragmentForwardMessageBinding;
import com.hisu.zola.listeners.IOnForwardMessageCheckChangeListener;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;
import com.hisu.zola.util.socket.SocketIOHandler;
import com.hisu.zola.view_model.ConversationListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForwardMessageFragment extends Fragment {
    private static final String FORWARD_MSG_ARGS = "FORWARD_MSG_ARGS";

    private MainActivity mainActivity;
    private FragmentForwardMessageBinding mBinding;
    private ConversationListViewModel viewModel;
    private ForwardMessageAdapter forwardMessageAdapter;
    private Message forwardMessage;
    private List<Conversation> conversationsID;
    private LoadingDialog loadingDialog;

    public static ForwardMessageFragment newInstance(Message forwardMessage) {
        ForwardMessageFragment fragment = new ForwardMessageFragment();
        Bundle args = new Bundle();
        args.putSerializable(FORWARD_MSG_ARGS, forwardMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            forwardMessage = (Message) getArguments().getSerializable(FORWARD_MSG_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentForwardMessageBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        init();
        addActionForBtnCancel();
        addActionForBtnDone();
    }

    private void init() {
        conversationsID = new ArrayList<>();

        forwardMessageAdapter = new ForwardMessageAdapter(mainActivity);

        viewModel = new ViewModelProvider(mainActivity).get(ConversationListViewModel.class);
        viewModel.getData().observe(mainActivity, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {

                if (conversations == null) return;

                List<Conversation> curConversations = new ArrayList<>();
                conversations.forEach(conversation -> {
                    conversation.getMember().forEach(member -> {
                        if (member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId()))
                            curConversations.add(conversation);
                    });
                });

                forwardMessageAdapter.setConversations(curConversations);
                mBinding.rvForwardConversation.setAdapter(forwardMessageAdapter);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        linearLayoutManager.setStackFromEnd(true);

        mBinding.rvForwardConversation.setLayoutManager(
                linearLayoutManager
        );

        mBinding.rvForwardConversation.setAdapter(forwardMessageAdapter);
        forwardMessageAdapter.setOnItemCheckedChangListener(new IOnForwardMessageCheckChangeListener() {
            @Override
            public void itemCheck(Conversation conversation, boolean isCheck) {

                if (isCheck) {
                    conversationsID.add(conversation);
                } else {
                    for (Conversation conversationId : conversationsID) {
                        if (conversationId.getId().equalsIgnoreCase(conversation.getId())) {
                            conversationsID.remove(conversationId);
                            break;
                        }
                    }
                }

                if (conversationsID.size() > 0)
                    mBinding.iBtnDone.setVisibility(View.VISIBLE);
                else
                    mBinding.iBtnDone.setVisibility(View.GONE);
            }
        });
    }

    private void addActionForBtnCancel() {
        mBinding.iBtnCancel.setOnClickListener(view -> {
            backToPrevPage();
        });
    }

    private void backToPrevPage() {
        mainActivity.setBottomNavVisibility(View.GONE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    private void addActionForBtnDone() {
        mBinding.iBtnDone.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mainActivity))
                forwardMessage();
            else
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.no_network_connection))
                        .setSubtitle(mainActivity.getString(R.string.no_network_connection_desc))
                        .setPositiveListener(mainActivity.getString(R.string.confirm), iOSDialog::dismiss).build().show();
        });
    }

    private void forwardMessage() {
        for (Conversation conversation : conversationsID) {
            sendMessageViaApi(mainActivity, conversation, forwardMessage);
        }
    }

    private void sendMessageViaApi(MainActivity mainActivity, Conversation conversation, Message message) {

        mainActivity.runOnUiThread(() -> {
            loadingDialog.showDialog();
        });

        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        object.add("conversation", gson.toJsonTree(conversation));
        object.addProperty("sender", LocalDataManager.getCurrentUserInfo().getId());
        object.addProperty("text", message.getText());
        object.addProperty("type", message.getType());
        object.add("media", gson.toJsonTree(message.getMedia()));

        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.sendMessage(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    String json = gson.toJson(response.body());

                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    Message message = gson.fromJson(obj.get("data"), Message.class);
                    for (Conversation sentConversation : conversationsID) {
                        if (sentConversation.getId().equalsIgnoreCase(conversation.getId())) {
                            conversationsID.remove(sentConversation);
                            break;
                        }
                    }

                    sendMessage(mainActivity, conversation, message);
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
                Log.e(ForwardMessageFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void sendMessage(MainActivity mainActivity, Conversation conversation, Message message) {
        Socket mSocket = SocketIOHandler.getInstance().getSocketConnection();
        MessageRepository messageRepository = new MessageRepository(mainActivity.getApplication());
        ConversationRepository conversationRepository = new ConversationRepository(mainActivity.getApplication());

        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Gson gson = new Gson();

        messageRepository.insertOrUpdate(message);
        conversation.setLastMessage(message);
        conversationRepository.insertOrUpdate(conversation);

        JsonObject emitMsg = new JsonObject();
        emitMsg.add("conversation", gson.toJsonTree(conversation));
        emitMsg.add("sender", gson.toJsonTree(LocalDataManager.getCurrentUserInfo()));

        emitMsg.addProperty("text", message.getText());
        emitMsg.addProperty("type", message.getType());
        emitMsg.add("media", gson.toJsonTree(message.getMedia()));
        emitMsg.addProperty("isDelete", message.getDeleted());
        emitMsg.addProperty("_id", message.getId());
        emitMsg.addProperty("createdAt", message.getCreatedAt());
        emitMsg.addProperty("updatedAt", message.getUpdatedAt());

        if (conversationsID.size() < 1) {
            mainActivity.runOnUiThread(() -> {
                loadingDialog.dismissDialog();
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.notification_warning))
                        .setCancelable(false)
                        .setSubtitle(mainActivity.getString(R.string.forward_message_done))
                        .setPositiveListener(mainActivity.getString(R.string.confirm), dialog -> {
                            dialog.dismiss();
                            mainActivity.getSupportFragmentManager().popBackStackImmediate();
                        }).build().show();
            });
        }

        mSocket.emit(Constraints.EVT_MESSAGE_SEND, emitMsg);
    }
}