package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        binding = FragmentConversationBinding.inflate(inflater, container, false);

        addToggleShowSendIcon();

        loadConversation("1");

        binding.rvConversation.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        return binding.getRoot();
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

                    List<Message> messages = response.body() != null ?
                            response.body().getMessages() : new ArrayList<>();

                    binding.rvConversation.setAdapter(new MessageAdapter(messages, mainActivity));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                Log.e("API_Msg_Call", t.getMessage());
            }
        });
    }
}