package com.hisu.zola.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hisu.zola.BuildConfig;
import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.entity.Conversation;
import com.hisu.zola.entity.Message;
import com.hisu.zola.util.ApiService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment {

    private FragmentConversationBinding binding;
    private MainActivity mainActivity;
    private List<Message> messages;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        binding = FragmentConversationBinding.inflate(inflater, container, false);

        initRecyclerView();

        getConversationInfo();
        addActionForBackBtn();
        addActionForAudioCallBtn();
        addActionForVideoCallBtn();

        addActionForSendMessageBtn();
        addToggleShowSendIcon();

        loadConversation("1");

        return binding.getRoot();
    }

    private void initRecyclerView() {
        binding.rvConversation.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        messages = new ArrayList<>();
    }

//  Todo: fake conversation info
    private void getConversationInfo() {
        binding.tvUsername.setText("Harry");
        binding.tvLastActive.setText("Vừa mới truy cập");
    }

//  Todo: will change later to go back to prev page
    private void addActionForBackBtn() {
        binding.btnBack.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "Back to previous", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForAudioCallBtn() {
        binding.btnAudioCall.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "Audio call", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForVideoCallBtn() {
        binding.btnVideoCall.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "Video call", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForSendMessageBtn() {
        binding.btnSend.setOnClickListener(view -> {
            sendMessage(null);
        });
    }

    private void sendMessage(Message message) {
        if(binding.btnSend.getVisibility() != View.VISIBLE) return;

//      Todo: send msg flow goes here

        binding.edtChat.setText("");
        closeSoftKeyboard();
        binding.rvConversation.smoothScrollToPosition(messages.size() -1);
    }

    private void closeSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) mainActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );

        manager.hideSoftInputFromWindow(binding.edtChat.getWindowToken(), 0);
    }

    private void addToggleShowSendIcon() {
        binding.edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = editable.length();

                if (textLength > 0)
                    binding.btnSend.setVisibility(View.VISIBLE);
                else
                    binding.btnSend.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loadConversation(String conversationID) {
        ApiService.apiService.getConversation(conversationID).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
                if(response.isSuccessful()) {
                    messages = response.body() != null ?
                            response.body().getMessages() : new ArrayList<>();

                    binding.rvConversation.setAdapter(new MessageAdapter(messages, mainActivity));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                Log.e("MSG_API_ERR", t.getMessage());
            }
        });
    }
}