package com.hisu.zola.fragments.conversation;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.entity.Message;
import com.hisu.zola.fragments.HomeFragment;
import com.hisu.zola.util.SocketIOHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {

    public static final String CONVERSATION_ID_ARGS = "CONVERSATION_ID";

    private FragmentConversationBinding mBinding;
    private MainActivity mMainActivity;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private Socket mSocket;

    public static ConversationFragment newInstance(String conversationID) {
        Bundle args = new Bundle();
        args.putString(CONVERSATION_ID_ARGS, conversationID);

        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentConversationBinding.inflate(inflater, container, false);

        mSocket = SocketIOHandler.getSocketConnection();
        mSocket.on("ReceiveMessage", onMessageReceive);

        initRecyclerView();

        getConversationInfo();
        addActionForBackBtn();
        addActionForAudioCallBtn();
        addActionForVideoCallBtn();
        addActionForSideMenu();

        addActionForSendMessageBtn();
        addToggleShowSendIcon();

        String conversationID = getArguments() != null ?
                getArguments().getString(CONVERSATION_ID_ARGS) : "";

        loadConversation(conversationID);

        return mBinding.getRoot();
    }

    private void initRecyclerView() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, mMainActivity);

        mBinding.rvConversation.setLayoutManager(
                new LinearLayoutManager(
                        mMainActivity, LinearLayoutManager.VERTICAL, false
                )
        );
    }

    //  Todo: fake conversation info
    private void getConversationInfo() {
        mBinding.tvUsername.setText("Harry");
        mBinding.tvLastActive.setText(getString(R.string.user_active));
    }

    private void addActionForBackBtn() {
        mBinding.btnBack.setOnClickListener(view -> {
            mMainActivity.setFragment(HomeFragment.newInstance(HomeFragment.NORMAL_ARGS));
        });
    }

    private void addActionForAudioCallBtn() {
        mBinding.btnAudioCall.setOnClickListener(view -> {
            Toast.makeText(mMainActivity, "Audio call", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForVideoCallBtn() {
        mBinding.btnVideoCall.setOnClickListener(view -> {
            Toast.makeText(mMainActivity, "Video call", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForSideMenu() {
        mBinding.btnConversationMenu.setOnClickListener(view -> {
            Toast.makeText(mMainActivity, "Side menu", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForSendMessageBtn() {
        mBinding.btnSend.setOnClickListener(view -> {
            sendMessage(new Message("1", mBinding.edtChat.getText().toString().trim()));
        });
    }

    private void sendMessage(Message message) {
        if (mBinding.btnSend.getVisibility() != View.VISIBLE) return;

        mSocket.emit("NewMessage", new Gson().toJson(message));

        closeSoftKeyboard();

        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();

        messages.add(message);
        messageAdapter.setMessages(messages);
        messageAdapter.notifyItemInserted(messages.indexOf(message));

        smoothScrollToLatestMsg();
    }

    private void closeSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) mMainActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );

        manager.hideSoftInputFromWindow(mBinding.edtChat.getWindowToken(), 0);
    }

    private void addToggleShowSendIcon() {
        mBinding.edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = editable.toString().trim().length();

                if (textLength > 0) {
                    mBinding.btnSend.setVisibility(View.VISIBLE);
                    mBinding.btnSendImg.setVisibility(View.GONE);
                } else {
                    mBinding.btnSend.setVisibility(View.GONE);
                    mBinding.btnSendImg.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadConversation(String conversationID) {

        messageAdapter.setMessages(messages);
        mBinding.rvConversation.setAdapter(messageAdapter);
    }

    private void smoothScrollToLatestMsg() {
        mBinding.rvConversation.smoothScrollToPosition(messages.size() - 1);
    }

    private final Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {
                    Message message = new Gson().fromJson(data.toString(), Message.class);
                    messages.add(message);
                    messageAdapter.setMessages(messages);
                    messageAdapter.notifyItemInserted(messages.indexOf(message));
                    smoothScrollToLatestMsg();
                }
            });
        }
    };
}