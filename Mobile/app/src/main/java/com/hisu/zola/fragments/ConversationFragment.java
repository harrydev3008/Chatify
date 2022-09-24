package com.hisu.zola.fragments;

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

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {

    private FragmentConversationBinding mBinding;
    private MainActivity mMainActivity;
    private List<Message> messages;
    private MessageAdapter messageAdapter;

    public static ConversationFragment newInstance(String conversationID) {
        Bundle args = new Bundle();
        args.putString("conversationID", conversationID);

        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentConversationBinding.inflate(inflater, container, false);

        initRecyclerView();

        getConversationInfo();
        addActionForBackBtn();
        addActionForAudioCallBtn();
        addActionForVideoCallBtn();

        addActionForSendMessageBtn();
        addToggleShowSendIcon();

        String conversationID = getArguments() != null ?
                getArguments().getString("conversationID") : "";

        loadConversation(conversationID);

        return mBinding.getRoot();
    }

    private void initRecyclerView() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, mMainActivity);
        mBinding.rvConversation.setAdapter(messageAdapter);

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
            mMainActivity.getSupportFragmentManager().popBackStack();
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

    private void addActionForSendMessageBtn() {
        mBinding.btnSend.setOnClickListener(view -> {
            sendMessage(new Message("1", mBinding.edtChat.getText().toString().trim()));
        });
    }

    private void sendMessage(Message message) {
        if (mBinding.btnSend.getVisibility() != View.VISIBLE) return;

        messages.add(message);
        messageAdapter.setMessages(messages);

        closeSoftKeyboard();

        mBinding.edtChat.setText("");
        mBinding.edtChat.requestFocus();

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
//        ApiService.apiService.getConversation(conversationID).enqueue(new Callback<Conversation>() {
//            @Override
//            public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
//                if(response.isSuccessful()) {
//                    messages = response.body() != null ?
//                            response.body().getMessages() : new ArrayList<>();
//
//                    messageAdapter.setMessages(messages);
//                    smoothScrollToLatestMsg();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
//                Log.e("MSG_API_ERR", t.getMessage());
//            }
//        });
    }

    private void smoothScrollToLatestMsg() {
        mBinding.rvConversation.smoothScrollToPosition(messages.size() - 1);
    }
}