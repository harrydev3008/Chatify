package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.MessageAdapter;
import com.hisu.zola.databinding.FragmentConversationBinding;
import com.hisu.zola.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public class ConversationFragment extends Fragment {

    private FragmentConversationBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        binding = FragmentConversationBinding.inflate(inflater, container, false);

        addToggleShowSendIcon();

        List<Message> messages = List.of(
                new Message("test", "Hi bro! wassup!", LocalDateTime.now()),
                new Message("harry", "eyyo! doing good man", LocalDateTime.now()),
                new Message("test", "Yeah great to hear", LocalDateTime.now()),
                new Message("harry", ":D yeah", LocalDateTime.now())
        );

        MessageAdapter messageAdapter = new MessageAdapter(messages, mainActivity);

        binding.rvConversation.setAdapter(messageAdapter);
        binding.rvConversation.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));

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
}